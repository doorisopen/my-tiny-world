package com.twlee.bank.account.event.handler;

import com.twlee.bank.account.event.AccountChangedEvent;
import com.twlee.bank.common.application.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AccountChangedHandler {
    private final NotificationService consoleNotification;
    private final NotificationService slackNotification;

    public AccountChangedHandler(NotificationService consoleNotification,
                                 NotificationService slackNotification) {
        this.consoleNotification = consoleNotification;
        this.slackNotification = slackNotification;
    }

    @EventListener(AccountChangedEvent.class)
    public void handle(AccountChangedEvent event) {
        consoleNotification.send(event.toString());
        slackNotification.send(event.toString());
    }
}
