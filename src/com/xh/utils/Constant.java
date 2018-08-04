package com.xh.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class Constant {
    public static final HashMap<String, String> paths = new HashMap();
    public static final ArrayList<String> adapters = new ArrayList();

    static
    {
        paths.put("WebView", "android.webkit");
        paths.put("View", "android.view");
        paths.put("ViewStub", "android.view");
        paths.put("SurfaceView", "android.view");
        paths.put("TextureView", "android.view");

        adapters.add("android.widget.ListAdapter");
        adapters.add("android.widget.ArrayAdapter");
        adapters.add("android.widget.BaseAdapter");
        adapters.add("android.widget.HeaderViewListAdapter");
        adapters.add("android.widget.SimpleAdapter");
        adapters.add("android.support.v4.widget.CursorAdapter");
        adapters.add("android.support.v4.widget.SimpleCursorAdapter");
        adapters.add("android.support.v4.widget.ResourceCursorAdapter");
    }
}
