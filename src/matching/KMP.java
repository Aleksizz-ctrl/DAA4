package matching;

import java.util.ArrayList;
import java.util.List;

public class KMP {
    public long comparisons = 0;

    public int[] lps(String p) {
        int[] lps = new int[p.length()];
        int len = 0, i = 1;
        while (i < p.length()) {
            comparisons++;
            if (p.charAt(i) == p.charAt(len)) lps[i++] = ++len;
            else if (len > 0) len = lps[len - 1];
            else lps[i++] = 0;
        }
        return lps;
    }

    public List<Integer> search(String text, String pattern) {
        List<Integer> res = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0 || m > n) return res;
        int[] table = lps(pattern);
        int i = 0, j = 0;
        while (i < n) {
            comparisons++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) { res.add(i - m); j = table[j - 1]; }
            } else if (j > 0) j = table[j - 1];
            else i++;
        }
        return res;
    }
}