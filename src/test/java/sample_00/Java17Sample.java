package sample_00;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.util.List;

import javax.crypto.SealedObject;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Java8⇒Java17 コーディング関連変更点
 * *****************************************************************************
 *
 * ①ローカル変数の型推論（Java10）
 *   ⇒ラムダ式引数へのvar使用：アノテーション利用可（Java11）
 *
 * ②switch式：分岐結果が値としても利用可（Java14）
 *
 * ③テキストブロック：改行付き文字列を「見た目通り」に定義（Java15）
 *
 * ④instanceofのパターンマッチング：型チェック＋変数定義を簡素に記述（Java16）
 *
 * ⑤record：値を保持するだけのクラスを手軽に定義（Java16）
 *
 * ⑥Sealed：継承先を限定できるように（Java17）
 *
 * *****************************************************************************
 */
@TestMethodOrder(OrderAnnotation.class)
class Java17Sample {

    /**
     * ①ローカル変数の型推論
     *   ⇒ラムダ式引数へのvar使用：アノテーション利用可
     */
    @Nested
    class ローカル変数の型推論 {

        @Test
        @Order(1)
        void varの書き方() {

            // 右辺の値から型を推測してくれる
            var stringValue = "aaa"; // String
            var intValue = 10; // int

            // 初期値の無い値は不可
            //var obj1;
            //var obj2 = null;

            assertTrue(stringValue instanceof String);
        }

        @Test
        @Order(2)
        void var配列の書き方() {

            // 配列の初期化
            // 型を指定すれば可
    //        var array1 = { 1, 2, 3 };
            var array2 = new String[]{ "a", "b", "c"};

            assertTrue(array2[0] instanceof String);


            // fou文での使用も可
            for (var i = 0; i < 10; i++) {
                  System.out.println(i);
            }

            var list = List.of("a", "b", "c");
            for (var s : list) {
              System.out.println(s);
                assertTrue(s instanceof String);
            }
        }

        // ---------------------------------
        // ラムダ式引数へのvar使用
        // アノテーション利用可
        // ---------------------------------
        @Test
        @Order(3)
        void ラムダ式のvarの使用() {

            // ラムダ式で関数型インタフェースを実装
            FunctionalIf func = (@NonNull var str) -> str + "+";

            // 実装したメソッドを利用
            assertEquals(func.appendPlus("abc"), "abc+");

            // @NonNullを使用することでコンパイルエラーが発生、検知できるようになった
//            System.out.println(func.appendPlus(null));
        }

        @FunctionalInterface
        public interface FunctionalIf {
            public String appendPlus(String str);
        }

    }

    /**
     * ②switch式：分岐結果が値としても利用可
     *
     */
    @Nested
    class switch式 {

        @Test
        @Order(4)
        void yieldの書き方() {

            var day = DayOfWeek.SUNDAY;  // SUNDAY = 7

            // 「yield」の後ろに値（式）を書く
            // switch式の結果としてその値を返す
            var switchVal1 = switch (day) {
                case MONDAY:    yield 1;
                case TUESDAY:   yield 2;
                case WEDNESDAY: yield 3;
                case THURSDAY:  yield 4;
                case FRIDAY:    yield 5;
                case SATURDAY:  yield 6;
                default:        yield day.getValue();
            };
            assertEquals(7, switchVal1);
        }

        @Test
        @Order(5)
        void アロー構文の書き方() {

            var day = DayOfWeek.SUNDAY;

            // 「case 値 -> 式;」
            var switchVal2 = switch (day) {
                case MONDAY    -> 1;
                case TUESDAY   -> 2;
                case WEDNESDAY -> 3;
                case THURSDAY  -> 4;
                case FRIDAY    -> 5;
                case SATURDAY  -> 6;
                default        -> day.getValue();
            };

            assertEquals(7, switchVal2);
        }
    }


    /**
     * ③テキストブロック：改行付き文字列を「見た目通り」に定義
     *
     */
    @Test
    @Order(6)
    void テキストブロックの書き方() {

        // 複数行の文字列を表す場合
        String html1 = "<HTML>" +
                "\n\s\s\s\s" + "<BODY>" +
                "\n\s\s\s\s\s\s\s\s" + "<H1>\"Hello world!\"</H1>" +
                "\n\s\s\s\s" + "</BODY>" +
                "\n" + "</HTML>";


        // StringBuilderを使用
        StringBuilder sb = new StringBuilder();
        sb.append("<HTML>\n");
        sb.append("\s\s\s\s<BODY>\n");
        sb.append("\s\s\s\s\s\s\s\s<H1>\"Hello world!\"</H1>\n");
        sb.append("\s\s\s\s</BODY>\n");
        sb.append("</HTML>");
        String html2 = sb.toString();


        // 3つの二重引用符「"""」で囲む
        // 改行を表す「\n\t」などを省略可
        String html3 = """
<HTML>
    <BODY>
        <H1>"Hello world!"</H1>
    </BODY>
</HTML>""";


        assertEquals(html1, html2);
        assertEquals(html2, html3);
    }


    /**
     * ④instanceofのパターンマッチング：型チェック＋変数定義を簡素に記述
     * @throws Exception
     *
     */
    @Test
    @Order(7)
    void instanceofMatching() throws Exception {

        Object obj = "aaa";

        // before
        // まずobjがString型かどうかチェック
        // 変数sに定義
        if (obj instanceof String) {

            String s = (String) obj;
            System.out.println(s);
        }

        // 「instanceof」の後に型と変数を記載
        if (!(obj instanceof String s)){

//    		System.out.println(s);  // sは使用不可（スコープ外）
            throw new Exception();

        } else {
            // sが使用可能（スコープ内）
            System.out.println(s);
        }
    }


    /**
     * ⑤record：値を保持するだけのクラスを手軽に定義
     */
    @Nested
    class Record処理の追加 {

        public record Point(int x, int y) { }

//    public class Point {
//
//        private final int x;
//        private final int y;
//
//        Point(int x, int y) {
//            this.x = x;
//            this.y = y;
//        }
//
//        int x() { return x; }
//        int y() { return y; }
//
//        public boolean equals(Object o) {
//            if (!(o instanceof Point)) return false;
//            Point other = (Point) o;
//            return other.x == x && other.y == y;
//        }
//
//        public int hashCode() {
//            return Objects.hash(x, y);
//        }
//
//        public String toString() {
//            return String.format("Point[x=%d, y=%d]", x, y);
//        }
// }

        @Test
        @Order(8)
        void recordテスト1() {

            var p = new Point(1, 2);

            assertEquals(1, p.x());
            assertEquals(2, p.y());
            assertEquals("Point[x=1, y=2]", p.toString());


        }


        // コンストラクタで値チェックをする
        public record Check(int x, int y) {

            public Check {
                if (x > y) throw new IllegalArgumentException();
            }
        }

        @Test
        @Order(9)
        void recordテスト2() {

            var c1 = new Check(1, 2);

            assertEquals(1, c1.x());
            assertEquals(2, c1.y());

            try {
                // 「x > y」のため、エラー発生
                var c2 = new Check(3, 2);

            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }


    /**
     * ⑥Sealed：継承先を限定できるように
     *
     */
    @Nested
    public abstract sealed class Super permits A, B {}

    // 継承可
    @Nested
    final class A extends Super {}

    // 継承可
    @Nested
    final class B extends Super {}

//    継承不可
//    @Nested
//    final class C extends Super {}

}
