import dp.EditDistance;
import dp.LCS;
import lcp.Kasai;
import lcp.SimpleSuffixArray;
import matching.KMP;
import matching.Naive;
import matching.RabinKarp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tests {
    static int passed = 0, failed = 0;

    static void check(boolean cond, String name) {
        if (cond) passed++; else { failed++; System.out.println("FAILED: " + name); }
    }

    public static void main(String[] args) {
        testLcs();
        testEditDistance();
        testMatching();
        testRandomMatching();
        testKasai();
        System.out.println("Passed: " + passed + ", Failed: " + failed);
    }

    static void testLcs() {
        check(LCS.length("abc", "abc") == 3, "lcs identical");
        check(LCS.length("abc", "xyz") == 0, "lcs different");
        check(LCS.length("", "abc") == 0 && LCS.length("abc", "") == 0 && LCS.length("", "") == 0, "lcs empty");
        check(LCS.length("ABCBDAB", "BDCABA") == 4, "lcs classic");
        check(LCS.length("AGGTAB", "GXTXAYB") == 4, "lcs second");
    }

    static void testEditDistance() {
        check(EditDistance.distance("abc", "abc") == 0, "edit identical");
        check(EditDistance.distance("", "abc") == 3 && EditDistance.distance("abc", "") == 3, "edit one empty");
        check(EditDistance.distance("", "") == 0, "edit both empty");
        check(EditDistance.distance("cat", "cut") == 1, "edit substitution");
        check(EditDistance.distance("cat", "cart") == 1, "edit insertion");
        check(EditDistance.distance("cart", "cat") == 1, "edit deletion");
        check(EditDistance.distance("kitten", "sitting") == 3, "edit kitten");
        check(EditDistance.distance("sunday", "saturday") == 3, "edit sunday");
    }

    static void checkMatch(String text, String pat, List<Integer> expected, String name) {
        check(new Naive().search(text, pat).equals(expected), name + " naive");
        check(new KMP().search(text, pat).equals(expected), name + " kmp");
        check(new RabinKarp().search(text, pat).equals(expected), name + " rabin-karp");
        check(new RabinKarp(7).search(text, pat).equals(expected), name + " rabin-karp small modulus");
    }

    static void testMatching() {
        checkMatch("abcdef", "abc", List.of(0), "beginning");
        checkMatch("xxabcxx", "abc", List.of(2), "middle");
        checkMatch("xxxabc", "abc", List.of(3), "end");
        checkMatch("abababa", "aba", List.of(0, 2, 4), "multiple overlapping");
        checkMatch("aaaa", "aa", List.of(0, 1, 2), "all positions");
        checkMatch("abcdef", "xyz", List.of(), "absent");
        checkMatch("ab", "abc", List.of(), "pattern longer than text");
        checkMatch("abc", "", List.of(), "empty pattern");
        checkMatch("", "a", List.of(), "empty text");
        checkMatch("abc", "abc", List.of(0), "pattern equals text");
        checkMatch("ABABDABACDABABCABAB", "ABABCABAB", List.of(10), "kmp classic");

        check(Arrays.equals(new KMP().lps("ABABCABAB"), new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4}), "lps classic");
        check(Arrays.equals(new KMP().lps("aaaa"), new int[]{0, 1, 2, 3}), "lps aaaa");
        check(Arrays.equals(new KMP().lps("abcd"), new int[]{0, 0, 0, 0}), "lps abcd");
    }

    static void testRandomMatching() {
        Random r = new Random(1);
        for (int t = 0; t < 500; t++) {
            int k = 2 + r.nextInt(2);
            String text = randomString(r.nextInt(41), k, r);
            String pat = randomString(r.nextInt(7), k, r);
            checkMatch(text, pat, oracle(text, pat), "random " + t);
        }
    }

    static String randomString(int len, int alphabet, Random r) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((char) ('a' + r.nextInt(alphabet)));
        return sb.toString();
    }

    static List<Integer> oracle(String text, String pat) {
        List<Integer> res = new ArrayList<>();
        if (pat.isEmpty()) return res;
        int i = text.indexOf(pat);
        while (i >= 0) { res.add(i); i = text.indexOf(pat, i + 1); }
        return res;
    }

    static void testKasai() {
        int[] saBanana = SimpleSuffixArray.build("banana");
        check(Arrays.equals(saBanana, new int[]{5, 3, 1, 0, 4, 2}), "suffix array banana");
        check(Arrays.equals(new Kasai().build("banana", saBanana), new int[]{0, 1, 3, 0, 0, 2}), "lcp banana");

        int[] saAbab = {2, 0, 3, 1};
        check(Arrays.equals(new Kasai().build("abab", saAbab), new int[]{0, 2, 0, 1}), "lcp abab");

        check(new Kasai().build("", new int[0]).length == 0, "lcp empty string");
        check(Arrays.equals(new Kasai().build("a", new int[]{0}), new int[]{0}), "lcp single char");

        Random r = new Random(2);
        for (int t = 0; t < 200; t++) {
            String s = randomString(1 + r.nextInt(25), 2, r);
            int[] sa = SimpleSuffixArray.build(s);
            check(isSorted(s, sa), "suffix array sorted " + t);
            check(Arrays.equals(new Kasai().build(s, sa), bruteLcp(s, sa)), "lcp random " + t);
        }
    }

    static boolean isSorted(String s, int[] sa) {
        for (int i = 1; i < sa.length; i++)
            if (s.substring(sa[i - 1]).compareTo(s.substring(sa[i])) >= 0) return false;
        return true;
    }

    static int[] bruteLcp(String s, int[] sa) {
        int[] lcp = new int[sa.length];
        for (int i = 1; i < sa.length; i++) {
            int a = sa[i - 1], b = sa[i], h = 0;
            while (a + h < s.length() && b + h < s.length() && s.charAt(a + h) == s.charAt(b + h)) h++;
            lcp[i] = h;
        }
        return lcp;
    }
}
