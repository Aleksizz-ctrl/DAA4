package matching;

import java.util.ArrayList;
import java.util.List;

public class Naive {
    public long comparisons = 0;

    public List<Integer> search(String text, String pattern) {
        List<Integer> res = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0 || m > n) return res;
        for (int i = 0; i + m <= n; i++) {
            int j = 0;
            while (j < m) {
                comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) break;
                j++;
            }
            if (j == m) res.add(i);
        }
        return res;
    }
}
