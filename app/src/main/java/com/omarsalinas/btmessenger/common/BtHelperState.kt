package com.omarsalinas.btmessenger.common

enum class BtHelperState(val value: Int) {
    IDLE(0x0001),
    LISTEN(0x0002),
    CONNECTING(0x0003),
    CONNECTED(0x0004),

    CONNECTION_LOST(0x0005),
    NO_CONNECTION(0x0006)
    ;

    companion object {
        fun fromValue(value: Int): BtHelperState {
            return BtHelperState.values().first { it.value == value }
        }
    }

}