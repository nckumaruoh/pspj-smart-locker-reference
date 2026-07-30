package edu.klh.pspj.smartlocker.util;

import edu.klh.pspj.smartlocker.exception.InvalidPickupCodeException;

/**
 * Generates and validates a five-digit teaching code.
 *
 * <p>The first four digits are the body. The fifth digit is the recursive
 * digit sum of the body modulo 10. This is a recursion demonstration, not a
 * security mechanism.</p>
 */
public final class PickupCodeUtil {
    private PickupCodeUtil() {
    }

    public static String createCode(int sequence) {
        int body = 1000 + Math.floorMod(sequence * 137, 9000);
        int checksum = digitSumRecursive(body) % 10;
        return String.format("%04d%d", body, checksum);
    }

    public static boolean isValid(String code) {
        if (code == null || !code.matches("\\d{5}")) {
            return false;
        }
        int body = Integer.parseInt(code.substring(0, 4));
        int suppliedChecksum = Character.digit(code.charAt(4), 10);
        return digitSumRecursive(body) % 10 == suppliedChecksum;
    }

    public static void requireValid(String code) throws InvalidPickupCodeException {
        if (!isValid(code)) {
            throw new InvalidPickupCodeException(
                    "Pickup code must contain five digits with a valid checksum.");
        }
    }

    public static int digitSumRecursive(int number) {
        int nonNegative = Math.abs(number);
        if (nonNegative < 10) {
            return nonNegative;
        }
        return nonNegative % 10 + digitSumRecursive(nonNegative / 10);
    }
}
