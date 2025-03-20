package me.hoyeon.shortlink.application;

public interface EmailSender {

  void send(String to, String subject, String content) throws EmailSendException;
}
