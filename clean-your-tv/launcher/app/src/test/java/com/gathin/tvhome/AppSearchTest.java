package com.gathin.tvhome;
import org.junit.Test;
import static org.junit.Assert.*;
public class AppSearchTest {
    @Test public void emptyQueryShowsCatalog(){assertTrue(AppSearch.matches("Spotify", "  "));assertTrue(AppSearch.matches("Spotify",null));}
    @Test public void ignoresCase(){assertTrue(AppSearch.matches("SmartTube","SMARTtube"));}
    @Test public void matchesAccentsWithoutKeyboardAccentEntry(){assertTrue(AppSearch.matches("T\u00e9l\u00e9vision fran\u00e7aise","television francaise"));}
    @Test public void termsCanBeReordered(){assertTrue(AppSearch.matches("YouTube Music","music youtube"));}
    @Test public void allTermsMustMatch(){assertFalse(AppSearch.matches("YouTube Music","music spotify"));}
    @Test public void preservesNonLatinLetters(){assertTrue(AppSearch.matches("\u0645\u0648\u0633\u064a\u0642\u0649","\u0645\u0648\u0633\u064a\u0642\u0649"));assertFalse(AppSearch.matches("\u0645\u0648\u0633\u064a\u0642\u0649","\u0641\u064a\u062f\u064a\u0648"));}
    @Test public void missingLabelDoesNotCrash(){assertFalse(AppSearch.matches(null,"spotify"));}
}
