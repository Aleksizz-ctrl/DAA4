package lcp;

public class Kasai {
    public long comparisons = 0;

    public int[] build(String s, int[] sa) {
        int n = s.length();
        int[] rank = new int[n], lcp = new int[n];
        for (int i = 0; i < n; i++) rank[sa[i]] = i;
        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] == 0) { h = 0; continue; }
            int j = sa[rank[i] - 1];
            while (i + h < n && j + h < n) {
                comparisons++;
                if (s.charAt(i + h) != s.charAt(j + h)) break;
                h++;
            }
            lcp[rank[i]] = h;
            if (h > 0) h--;
        }
        return lcp;
    }
}
