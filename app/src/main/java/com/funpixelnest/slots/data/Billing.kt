package com.funpixelnest.slots.data

/**
 * Coin-pack purchases. No billing library is integrated yet, so [PlaceholderBilling]
 * completes every purchase immediately (test builds). Replace it with a Google Play
 * Billing implementation of this interface before release.
 */
interface Billing {
    fun buy(productId: String, onPurchased: () -> Unit)
}

class PlaceholderBilling : Billing {
    override fun buy(productId: String, onPurchased: () -> Unit) = onPurchased()
}

/** Full-screen sheets that any screen can open. */
enum class Overlay { Store, Piggy, Profile }
