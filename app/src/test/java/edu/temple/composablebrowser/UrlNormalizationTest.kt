package edu.temple.composablebrowser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UrlNormalizationTest {
    @Test
    fun addsHttpsToBareHost() {
        assertEquals("https://wikipedia.com", normalizeWebUrl("wikipedia.com"))
    }

    @Test
    fun preservesExplicitHttpAndPath() {
        assertEquals("http://example.com/page", normalizeWebUrl(" http://example.com/page "))
    }

    @Test
    fun rejectsUnsupportedOrMalformedUrl() {
        assertNull(normalizeWebUrl("ftp://example.com"))
        assertNull(normalizeWebUrl("https://"))
        assertNull(normalizeWebUrl("bad url"))
    }
}
