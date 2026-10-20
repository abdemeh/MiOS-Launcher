package com.gathin.tvhome;
import org.junit.Test;
import static org.junit.Assert.*;
public class PopupPlacementTest {
 @Test public void homeCardMenuIsAboveAndCentered(){assertArrayEquals(new int[]{100,290},PopupPlacement.nearCard(100,600,400,780,300,300,1920,1080,20,10));}
 @Test public void firstAppsRowMenuFallsBelow(){assertArrayEquals(new int[]{100,290},PopupPlacement.nearCard(100,100,400,280,300,300,1920,1080,20,10));}
 @Test public void edgeCardsKeepTheirMenusOnScreen(){int[] p=PopupPlacement.nearCard(1750,700,1910,880,400,400,1920,1080,20,10);assertEquals(1500,p[0]);assertTrue(p[1]>=20);assertTrue(p[1]+400<=1060);}
 @Test public void constrainedHeightClampsRatherThanGoingOffScreen(){int[] p=PopupPlacement.nearCard(0,100,100,180,300,450,640,480,15,10);assertEquals(15,p[0]);assertEquals(15,p[1]);}
}
