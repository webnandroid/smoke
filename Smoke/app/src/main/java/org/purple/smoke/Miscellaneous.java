/*
** Copyright (c) Alexis Megas.
** All rights reserved.
**
** Redistribution and use in source and binary forms, with or without
** modification, are permitted provided that the following conditions
** are met:
** 1. Redistributions of source code must retain the above copyright
**    notice, this list of conditions and the following disclaimer.
** 2. Redistributions in binary form must reproduce the above copyright
**    notice, this list of conditions and the following disclaimer in the
**    documentation and/or other materials provided with the distribution.
** 3. The name of the author may not be used to endorse or promote products
**    derived from Smoke without specific prior written permission.
**
** SMOKE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
** IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
** OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
** IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
** INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
** NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
** DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
** THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
** (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
** SMOKE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.purple.smoke;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Miscellaneous
{
    private final static int INTEGER_BYTES = 4;
    private final static int LONG_BYTES = 8;

    public static String byteArrayAsHexString(byte bytes[])
    {
	if(bytes == null || bytes.length <= 0)
	    return "";

	try
	{
	    StringBuilder stringBuilder = new StringBuilder();

	    for(byte b : bytes)
		stringBuilder.append(String.format("%02x", b));

	    return stringBuilder.toString();
	}
	catch(Exception exception)
	{
	    return "";
	}
    }

    public static String byteArrayAsHexStringDelimited(byte bytes[],
						       char delimiter,
						       int offset)
    {
	if(bytes == null || bytes.length <= 0 || offset < 0)
	    return "";

	String string = byteArrayAsHexString(bytes);

	try
	{
	    StringBuilder stringBuilder = new StringBuilder();

	    for(int i = 0; i < string.length(); i += offset)
	    {
		stringBuilder.append(string.substring(i, i + offset));
		stringBuilder.append(delimiter);
	    }

	    if(stringBuilder.length() > 0 &&
	       stringBuilder.charAt(stringBuilder.length() - 1) == delimiter)
		return stringBuilder.substring(0, stringBuilder.length() - 1);
	    else
		return stringBuilder.toString();
	}
	catch(Exception exception)
	{
	    return "";
	}
    }

    public static String delimitString(String string,
				       char delimiter,
				       int offset)
    {
	if(offset < 0)
	    return "";

	try
	{
	    StringBuilder stringBuilder = new StringBuilder();

	    for(int i = 0; i < string.length(); i += offset)
	    {
		stringBuilder.append(string.substring(i, i + offset));
		stringBuilder.append(delimiter);
	    }

	    if(stringBuilder.length() > 0 &&
	       stringBuilder.charAt(stringBuilder.length() - 1) == delimiter)
		return stringBuilder.substring(0, stringBuilder.length() - 1);
	    else
		return stringBuilder.toString();
	}
	catch(Exception exception)
	{
	    return "";
	}
    }

    public static String formattedDigitalInformation(String bytes)
    {
	try
	{
	    DecimalFormat decimalFormat = new DecimalFormat("0.00");
	    StringBuilder stringBuilder = new StringBuilder();
	    long v = Integer.decode(bytes).longValue();

	    if(v < 1024)
	    {
		stringBuilder.append(decimalFormat.format(v));
		stringBuilder.append(" B");
	    }
	    else if(v < 1024 * 1024)
	    {
		stringBuilder.append(decimalFormat.format(v / (1.0 * 1024)));
		stringBuilder.append(" KiB");
	    }
	    else if(v < 1024 * 1024 * 1024)
	    {
		stringBuilder.append
		    (decimalFormat.format(v / (1.0 * 1024 * 1024)));
		stringBuilder.append(" MiB");
	    }
	    else
	    {
		stringBuilder.append
		    (decimalFormat.format(v / (1.0 * 1024 * 1024 * 1024)));
		stringBuilder.append(" GiB");
	    }

	    return stringBuilder.toString();
	}
	catch(Exception exception)
	{
	    return "";
	}
    }

    public static String niceBoolean(boolean state)
    {
	if(state)
	    return "True";
	else
	    return "False";
    }

    public static String sipHashIdFromData(byte bytes[])
    {
	SipHash sipHash = new SipHash();

	return byteArrayAsHexStringDelimited
	    (longToByteArray(sipHash.
			     hmac(bytes, Cryptography.keyForSipHash(bytes))),
	     ':', 2);
    }

    public static byte[] compressed(byte bytes[])
    {
	if(bytes == null || bytes.length <= 0)
	    return null;

	try
	{
	    ByteArrayOutputStream byteArrayOutputStream =
		new ByteArrayOutputStream(bytes.length);

	    try
	    {
		try(GZIPOutputStream gzipOutputStream =
		    new GZIPOutputStream(byteArrayOutputStream))
		{
		    gzipOutputStream.write(bytes);
		}
	    }
	    finally
	    {
		byteArrayOutputStream.close();
	    }

	    return byteArrayOutputStream.toByteArray();
	}
	catch(Exception exception)
	{
	}

	return null;
    }

    public static byte[] deepCopy(byte bytes[])
    {
	if(bytes == null || bytes.length <= 0)
	    return null;

	byte array[] = new byte[bytes.length];

	System.arraycopy(bytes, 0, array, 0, array.length);
	return array;
    }

    public static byte[] intToByteArray(int value)
    {
	try
	{
	    return ByteBuffer.allocate(INTEGER_BYTES).putInt(value).array();
	}
	catch(Exception exception)
	{
	    return null;
	}
    }

    public static byte[] joinByteArrays(byte[] ... data)
    {
	if(data == null)
	    return null;

	try
	{
	    int length = 0;

	    for(byte b[] : data)
		if(b != null && b.length > 0)
		    length += b.length;

	    if(length == 0)
		return null;

	    byte bytes[] = new byte[length];
	    int i = 0;

	    for(byte b[] : data)
		if(b != null && b.length > 0)
		{
		    System.arraycopy(b, 0, bytes, i, b.length);
		    i += b.length;
		}

	    return bytes; // data[0] + data[1] + ... + data[n - 1]
	}
	catch(Exception exception)
	{
	    return null;
	}
    }

    public static byte[] longToByteArray(long value)
    {
	try
	{
	    return ByteBuffer.allocate(LONG_BYTES).putLong(value).array();

	}
	catch(Exception exception)
	{
	    return null;
	}
    }

    public static byte[] uncompressed(byte bytes[])
    {
	if(bytes == null || bytes.length <= 0)
	    return null;

	try
	{
	    ByteArrayInputStream byteArrayInputStream =
		new ByteArrayInputStream(bytes);
	    ByteArrayOutputStream byteArrayOutputStream =
		new ByteArrayOutputStream();

	    try
	    {
		try(GZIPInputStream gzipInputStream =
		    new GZIPInputStream(byteArrayInputStream))
		{
		    byte buffer[] = new byte[1024];
		    int rc = 0;

		    while((rc = gzipInputStream.read(buffer)) > 0)
			byteArrayOutputStream.write(buffer, 0, rc);
		}
	    }
	    finally
	    {
		byteArrayInputStream.close();
		byteArrayOutputStream.close();
	    }

	    return byteArrayOutputStream.toByteArray();
	}
	catch(Exception exception)
	{
	}

	return null;
    }

    public static int countOf(StringBuilder stringBuilder, char character)
    {
	if(stringBuilder == null || stringBuilder.length() == 0)
	    return 0;

	int count = 0;

	for(int i = 0; i < stringBuilder.length(); i++)
	    if(character == stringBuilder.charAt(i))
		count += 1;

	return count;
    }

    public static long byteArrayToLong(byte bytes[])
    {
	if(bytes == null || bytes.length != LONG_BYTES)
	    return 0;

	try
	{
	    ByteBuffer byteBuffer = ByteBuffer.allocate(LONG_BYTES);

	    byteBuffer.put(bytes);
	    byteBuffer.flip();
	    return byteBuffer.getLong();
	}
	catch(Exception exception)
	{
	    return 0;
	}
    }

    public static void addMembersToMenu(Cryptography cryptography,
					Database database,
					Menu menu,
					int count,
					int position)
    {
	if(cryptography == null || database == null || menu == null)
	    return;

	ArrayList<ParticipantElement> arrayList =
	    database.readParticipants(cryptography, "");

	if(arrayList != null && arrayList.size() > 0)
	{
	    SubMenu subMenu = null;

	    if(count == menu.size())
		subMenu = menu.addSubMenu
		    (Menu.NONE,
		     Menu.NONE,
		     position,
		     "Chat Messaging Window");
	    else
		subMenu = menu.getItem(menu.size() - 1).getSubMenu();

	    if(subMenu == null)
		return;

	    subMenu.clear();

	    for(ParticipantElement participantElement : arrayList)
	    {
		if(participantElement == null)
		    continue;

		subMenu.add
		    (1,
		     participantElement.m_oid,
		     0,
		     participantElement.m_name +
		     " (" +
		     Miscellaneous.
		     delimitString(participantElement.m_sipHashId.
				   replace(":", ""), '-', 4).
		     toUpperCase() +
		     ")");
	    }

	    arrayList.clear();
	}
    }

    public static void enableChildren(View view, boolean state)
    {
	if(view == null)
	    return;
	else if(!(view instanceof ViewGroup))
	{
	    view.setEnabled(state);
	    return;
	}

	for(int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
	{
	    View child = ((ViewGroup) view).getChildAt(i);

	    enableChildren(child, state);
	}
    }

    public static void showErrorDialog(Context context, String error)
    {
	if(((Activity) context).isFinishing())
	    return;

	AlertDialog alertDialog = new AlertDialog.Builder(context).create();

	alertDialog.setButton
	    (AlertDialog.BUTTON_NEUTRAL, "Dismiss",
	     new DialogInterface.OnClickListener()
	     {
		 public void onClick(DialogInterface dialog, int which)
		 {
		     dialog.dismiss();
		 }
	     });
	alertDialog.setMessage(error);
	alertDialog.setTitle("Error");
	alertDialog.show();
    }

    public static void showNotification(Context context,
					Intent intent,
					View view)
    {
	if(context == null ||
	   intent == null ||
	   intent.getAction() == null ||
	   view == null)
	    return;

	if(intent.getAction().equals("org.purple.smoke.chat_message"))
	{
	    if(((Activity) context).isFinishing())
		return;

	    String message = intent.getStringExtra("org.purple.smoke.message");
	    String name = intent.getStringExtra("org.purple.smoke.name");
	    String sipHashId = intent.getStringExtra
		("org.purple.smoke.sipHashId");

	    if(message == null || name == null || sipHashId == null)
		return;

	    boolean purple = intent.getBooleanExtra
		("org.purple.smoke.purple", false);
	    long sequence = intent.getLongExtra
		("org.purple.smoke.sequence", 1);
	    long timestamp = intent.getLongExtra
		("org.purple.smoke.timestamp", 0);

	    State.getInstance().logChatMessage
		(message, name, sipHashId, purple, sequence, timestamp);
	    message = message.trim();

	    TextView textView1 = new TextView(context);
	    final PopupWindow popupWindow = new PopupWindow(context);

	    if(name.length() > 15)
	    {
		name = name.substring(0, 15);

		if(!name.endsWith("..."))
		{
		    if(name.endsWith(".."))
			name += ".";
		    else if(name.endsWith("."))
			name += "..";
		    else
			name += "...";
		}
	    }

	    if(message.length() > 15)
	    {
		message = message.substring(0, 15);

		if(!message.endsWith("..."))
		{
		    if(message.endsWith(".."))
			message += ".";
		    else if(message.endsWith("."))
			message += "..";
		    else
			message += "...";
		}
	    }

	    textView1.setBackgroundColor(Color.rgb(244, 200, 117));
	    textView1.setText
		("A message (" + message + ") from " + name +
		 " has arrived.");
	    textView1.setTextSize(16);
	    popupWindow.setContentView(textView1);
	    popupWindow.setOutsideTouchable(true);

	    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
	    {
		popupWindow.setHeight(300);
		popupWindow.setWidth(450);
	    }

	    popupWindow.showAtLocation
		(view, Gravity.START | Gravity.TOP, 75, 75);

	    try
	    {
		Ringtone ringtone = null;
		Uri notification = RingtoneManager.getDefaultUri
		    (RingtoneManager.TYPE_NOTIFICATION);

		ringtone = RingtoneManager.getRingtone(context, notification);
		ringtone.play();
	    }
	    catch(Exception e)
	    {
	    }

	    Handler handler = new Handler();

	    handler.postDelayed(new Runnable()
	    {
		@Override
		public void run()
		{
		    popupWindow.dismiss();
		}
	    }, 10000); // 10 Seconds
	}
    }

    public static void showPromptDialog
	(Context context,
	 DialogInterface.OnCancelListener cancelListener,
	 String prompt)
    {
	if(((Activity) context).isFinishing())
	    return;

	AlertDialog alertDialog = new AlertDialog.Builder(context).create();
	CheckBox checkBox1 = new CheckBox(context);

	State.getInstance().removeKey("dialog_accepted");
	alertDialog.setButton
	    (AlertDialog.BUTTON_NEGATIVE, "No",
	     new DialogInterface.OnClickListener()
	     {
		 public void onClick(DialogInterface dialog, int which)
		 {
		     State.getInstance().removeKey("dialog_accepted");
		     dialog.dismiss();
		 }
	     });
	alertDialog.setButton
	    (AlertDialog.BUTTON_POSITIVE, "Yes",
	     new DialogInterface.OnClickListener()
	     {
		 public void onClick(DialogInterface dialog, int which)
		 {
		     State.getInstance().setString("dialog_accepted", "true");
		     dialog.cancel();
		 }
	     });
	alertDialog.setMessage(prompt);
	alertDialog.setOnCancelListener(cancelListener); /*
							 ** We cannot wait
							 ** for a response.
							 */
	alertDialog.setTitle("Confirmation");
	alertDialog.setView(checkBox1);
	alertDialog.show();

	final Button button1 = alertDialog.getButton
	    (AlertDialog.BUTTON_POSITIVE);

	button1.setEnabled(false);
	checkBox1.setOnCheckedChangeListener
	    (new CompoundButton.OnCheckedChangeListener()
	    {
		@Override
		public void onCheckedChanged
		    (CompoundButton buttonView, boolean isChecked)
		{
		    button1.setEnabled(isChecked);
		}
	    });
	checkBox1.setText("Confirm");
    }

    public static void showTextInputDialog
	(Context context,
	 DialogInterface.OnCancelListener cancelListener,
	 String prompt,
	 String title)
    {
	if(((Activity) context).isFinishing())
	    return;

	AlertDialog alertDialog = new AlertDialog.Builder(context).create();
	final EditText editText = new EditText(context);
	final boolean contextIsChat = context instanceof Chat;
	final boolean contextIsSettings = context instanceof Settings;

	alertDialog.setButton
	    (AlertDialog.BUTTON_NEGATIVE, "Cancel",
	     new DialogInterface.OnClickListener()
	     {
		 public void onClick(DialogInterface dialog, int which)
		 {
		     if(contextIsChat)
			 State.getInstance().removeKey("chat_secret_input");
		     else if(contextIsSettings)
			 State.getInstance().removeKey
			     ("settings_participant_name_input");

		     dialog.dismiss();
		 }
	     });
	alertDialog.setButton
	    (AlertDialog.BUTTON_POSITIVE, "Accept",
	     new DialogInterface.OnClickListener()
	     {
		 public void onClick(DialogInterface dialog, int which)
		 {
		     if(contextIsChat)
			 State.getInstance().setString
			     ("chat_secret_input",
			      editText.getText().toString());
		     else if(contextIsSettings)
			 State.getInstance().setString
			     ("settings_participant_name_input",
			      editText.getText().toString());

		     dialog.cancel();
		 }
	     });
	alertDialog.setMessage(prompt);
	alertDialog.setOnCancelListener(cancelListener); /*
							 ** We cannot wait
							 ** for a response.
							 */
	alertDialog.setTitle(title);
	editText.setInputType(InputType.TYPE_CLASS_TEXT);
	alertDialog.setView(editText);
	alertDialog.show();
    }
}
