package com.gathin.tvhome;

import org.junit.Test;
import static org.junit.Assert.*;

public class InputLabelsTest {
    @Test public void unpluggedConsoleDoesNotKeepItsOldName() {
        assertNull(InputLabels.device(false, "HDMI PlayStation 5"));
    }
    @Test public void connectedConsoleUsesCecNameWithoutRepeatingHdmi() {
        assertEquals("PlayStation 5", InputLabels.device(true, "HDMI PlayStation 5"));
    }
    @Test public void physicalPortLabelsStayStableWithOrWithoutWhitespace() {
        assertEquals("HDMI 1", InputLabels.port("HDMI1"));
        assertEquals("HDMI 2", InputLabels.port(" HDMI 2 "));
    }
    @Test public void genericChildPortLabelIsNotMistakenForADevice() {
        assertNull(InputLabels.device(true, "HDMI 1"));
        assertNull(InputLabels.device(true, "HDMI"));
    }
    @Test public void missingCecNameIsAllowedForConnectedNonCecDevices() {
        assertNull(InputLabels.device(true, null));
        assertNull(InputLabels.device(true, "  "));
    }
    @Test public void differentConnectedDeviceUsesItsOwnReportedName() {
        assertEquals("Laptop", InputLabels.device(true, "Laptop"));
    }
}
