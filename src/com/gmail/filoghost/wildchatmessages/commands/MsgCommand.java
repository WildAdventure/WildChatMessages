/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.wildchatmessages.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import wild.api.command.CommandFramework;

import com.gmail.filoghost.wildchatmessages.WildChatMessages;

public class MsgCommand extends CommandFramework {

	public static Map<CommandSender, String> lastRecipients = new HashMap<>();
	
	
	public MsgCommand() {
		super(WildChatMessages.plugin, "msg");
	}
	
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		CommandValidate.isTrue(args.length > 1, "Utilizzo comando: /msg <giocatore> <messaggio>");
		
		CommandSender recipient = MsgCommand.getSenderFromName(args[0]);
		CommandValidate.notNull(recipient, "Quel giocatore non è online.");
		CommandValidate.isTrue(sender != recipient, "Non puoi scriverti messaggi da solo!");
		
		String message = StringUtils.join(args, " ", 1, args.length);
		sendPrivateMessage(sender, recipient, message);
	}
	
	
	public static void sendPrivateMessage(CommandSender sender, CommandSender recipient, String message) {
		sender.sendMessage(formatMessage("me", recipient.getName(), message));
		recipient.sendMessage(formatMessage(sender.getName(), "me", message));
		
		lastRecipients.put(sender, recipient.getName());
		lastRecipients.put(recipient, sender.getName());
		
		SocialSpyCommand.intercept(sender, recipient, message);
	}
	
	
	public static String formatMessage(String senderName, String recipientName, String message) {
		return "§3[§b" + senderName + " §3-> §b" + recipientName + "§3] §f" + message;
	}
	
	
	public static CommandSender getSenderFromName(String name) {
		if (name == null) {
			return null;
		}
		
		if (name.equalsIgnoreCase("console")) {
			return Bukkit.getConsoleSender();
		} else {
			return Bukkit.getPlayerExact(name);
		}
	}
	
	
	public static CommandSender getLastRecipientFor(CommandSender sender) {
		String lastRecipientName = lastRecipients.get(sender);
		if (lastRecipientName == null) {
			return null;
		}
		
		return getSenderFromName(lastRecipientName);
	}
	
}
