package com.anidari.homeworkdiary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationDismissedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// unused, the id is used to make the intent unqiue, dont really need to
		// unpack it here but wanted to make sure it was packed!
		@SuppressWarnings("unused")
		int notificationId = intent.getExtras().getInt(
				"com.anromus.homeworkdiary.notificationId");
		// stopping the service
		context.stopService(new Intent(context, NotificationService.class));
	}

}
