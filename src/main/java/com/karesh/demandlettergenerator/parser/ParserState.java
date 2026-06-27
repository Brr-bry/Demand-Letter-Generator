package com.karesh.demandlettergenerator.parser;

public enum ParserState {
    SEARCHING_CUSTOMER,
    WAITING_FOR_TRANSACTION_HEADER,
    READING_TRANSACTIONS
}