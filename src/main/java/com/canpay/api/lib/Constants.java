package com.canpay.api.lib;

/**
 * Utility class for application-wide constant values.
 */
public final class Constants {
    // Private constructor to prevent instantiation
    private Constants() {
    }

    /** Length of the wallet number (in digits) */
    public static final int WALLET_NUMBER_LENGTH = 16;

    /** Maximum number of attempts to generate a unique wallet number */
    public static final int WALLET_NUMBER_MAX_ATTEMPTS = 32;

    /** Modulo value used for wallet number generation (16 digits) */
    public static final long WALLET_NUMBER_MODULO = 1_0000_0000_0000_0000L;

    /** Base path for image storage */
    public static final String IMAGE_STORAGE_PATH = "/images/";
}
