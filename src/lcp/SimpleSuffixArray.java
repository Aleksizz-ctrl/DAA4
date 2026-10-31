package lcp;

import java.util.Arrays;

public class SimpleSuffixArray {

    public static int[] build(String s) {
        int n = s.length();
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (x, y) -> compare(s, x, y));
        int[] sa = new int[n];
        for (int i = 0; i < n; i++) sa[i] = idx[i];
        return sa;
    }

    private static int compare(String s, int x, int y) {
        int n = s.length();
        while (x < n && y < n) {
            int d = s.charAt(x) - s.charAt(y);
            if (d != 0) return d;
            x++;
            y++;
        }
        return (n - x) - (n - y);
    }
}