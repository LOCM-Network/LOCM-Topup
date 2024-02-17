package me.phuongaz.thesieure.card.type;

public enum CardType {
    VIETTEL,
    MOBIFONE,
    VINAPHONE,
    VIETNAMMOBILE,
    ZING,
    GATE,
    VCOIN;

    public static CardType fromString(String type) {
        for (CardType cardType : values()) {
            if (cardType.name().equalsIgnoreCase(type.toUpperCase())) {
                return cardType;
            }
        }
        return null;
    }

    public static boolean isCardType(String type) {
        for (CardType cardType : values()) {
            if (cardType.name().equalsIgnoreCase(type.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public static String[] getCardTypes() {
        String[] types = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            types[i] = values()[i].name();
        }
        return types;
    }
}
