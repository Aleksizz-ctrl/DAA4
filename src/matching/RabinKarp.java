package matching;

import java.util.ArrayList;
import java.util.List;

public class RabinKarp {
    private static final long BASE = 256;
    private final long mod;
    public long comparisons = 0;

    public RabinKarp() { this(1_000_000_007L); }

    public RabinKarp(long mod) { this.mod = mod; }

    public List<Integer> search(String text, String pattern) {
        List<Integer> res = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0 || m > n) return res;
        long ph = 0, th = 0, pow = 1;
        for (int i = 0; i < m; i++) {
            ph = (ph * BASE + pattern.charAt(i)) % mod;
            th = (th * BASE + text.charAt(i)) % mod;
            if (i > 0) pow = pow * BASE % mod;
        }
        for (int i = 0; i + m <= n; i++) {
            comparisons++;
            if (ph == th && same(text, i, pattern)) res.add(i);
            if (i + m < n) th = ((th - text.charAt(i) * pow % mod + mod) * BASE + text.charAt(i + m)) % mod;
        }
        return res;
    }

    private boolean same(String text, int start, String pattern) {
        for (int k = 0; k < pattern.length(); k++) {
            comparisons++;
            if (text.charAt(start + k) != pattern.charAt(k)) return false;
        }
        return true;
    }
}
