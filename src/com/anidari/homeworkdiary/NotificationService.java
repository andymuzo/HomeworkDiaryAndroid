package com.anidari.homeworkdiary;

import java.util.ArrayList;

import com.anromus.homeworkdiary.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class NotificationService extends Service {

	private NotificationManager nm;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

	}

	@Override
	public void onDestroy() {
		// gets rid of the activity
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		DatabaseHandler db = new DatabaseHandler(this);

		// get a list of the homework due tomorrow
		ArrayList<NotificationEntry> homework = db.getTomorrowsHomework();

		// if there is anything due then make a notification about it
		if (homework.size() != 0) {
			makeNotification(homework);
		}

		// create tomorrow's alarm manager
		AlarmHandler ah = new AlarmHandler(this.getApplicationContext());
		ah.unsetAlarm(this.getApplicationContext());
		ah.setAlarm(this.getApplicationContext());

		// stops the service from running if it has no notification to make
		if (homework.size() == 0) {
			this.stopSelf();
		}

//		super.onStartCommand(intent, flags, startId)
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private void makeNotification(ArrayList<NotificationEntry> homework) {
		Intent mainActivityIntent = new Intent(this, HomeworkActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0,
				mainActivityIntent, 0);

		// different notification content depending on amount and type due
		String contentText = null;
		if (homework.size() == 1) {
			contentText = homework.get(0).getSubject() + ", "
					+ homework.get(0).getTitle();
		} else {
			int finished = 0;
			int unfinished = 0;
			for (NotificationEntry n : homework) {
				if (n.isFinished()) {
					++finished;
				} else {
					++unfinished;
				}
			}

			contentText = "" + homework.size() + " "
					+ getString(R.string.notify_due) + " ";

			if (unfinished != 0) {
				contentText = contentText.concat("" + unfinished + " "
						+ getString(R.string.notify_unfinished));

				if (finished != 0) {
					contentText = contentText.concat(" "
							+ getString(R.string.and) + " ");
				}
			}
			if (finished != 0) {
				contentText = contentText.concat("" + finished + " "
						+ getString(R.string.notify_finished));
			}

		}

		Notification notification = new NotificationCompat.Builder(this)
				.setContentTitle(getString(R.string.notify_due_tomorrow))
				.setContentText(contentText)
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.setDeleteIntent(createOnDismissedIntent(this, R.id.notification_id))
				.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;

		nm.notify(R.id.notification_id, notification);
	}

	/** 
	 * Create the pending intent using a unique id for the pending intent (the
	 * notification id is used here) as without this the same extras will be
	 * reused for each dismissal event
	 */
	private PendingIntent createOnDismissedIntent(Context context,
			int notificationId) {
		Intent intent = new Intent(context, com.anidari.homeworkdiary.NotificationDismissedReceiver.class);
		intent.putExtra("com.anromus.homeworkdiary.notificationId", notificationId);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context.getApplicationContext(), notificationId, intent, 0);
		return pendingIntent;
	}
}