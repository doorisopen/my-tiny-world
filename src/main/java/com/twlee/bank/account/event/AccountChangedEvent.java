package com.twlee.bank.account.event;

import com.twlee.bank.account.domain.Cash;
import com.twlee.bank.account.domain.TransactionType;
import com.twlee.bank.common.event.Event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class AccountChangedEvent extends Event {
    private final Cash before;
    private final Cash after;
    private final TransactionType transactionType;

    public AccountChangedEvent(Cash before, Cash after, TransactionType transactionType) {
        this.before = before;
        this.after = after;
        this.transactionType = transactionType;
    }

    @Override
    public String toString() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(getTimestamp()), TimeZone.getDefault().toZoneId());
        return String.format(
                "[%s] tiny-bank-world 안내 %s %d원, 잔액 %d원",
                localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                switch (transactionType) {
                    case DEPOSIT -> "입금";
                    case WITHDRAW -> "출금";
                },
                before.getAmount().intValue(),
                after.getAmount().intValue());
    }
}
