package com.gathin.tvhome;

import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;

public class HomeSelectionOrderTest {
    @Test public void newlySelectedAppAppendsAfterExistingManualOrder() {
        assertEquals(Arrays.asList("spotify", "youtube", "iptv", "new-app"),
                Order.afterSelection(Arrays.asList("spotify", "youtube", "iptv"),
                        Arrays.asList("new-app", "youtube", "iptv", "spotify")));
    }
    @Test public void unselectedAppLeavesNoEmptySlot() {
        assertEquals(Arrays.asList("youtube", "iptv"),
                Order.afterSelection(Arrays.asList("youtube", "spotify", "iptv"),
                        Arrays.asList("iptv", "youtube")));
    }
    @Test public void repeatedSelectionDoesNotDuplicateShortcuts() {
        assertEquals(Arrays.asList("youtube", "spotify"),
                Order.afterSelection(Arrays.asList("youtube"), Arrays.asList("youtube", "spotify", "spotify")));
    }
}
