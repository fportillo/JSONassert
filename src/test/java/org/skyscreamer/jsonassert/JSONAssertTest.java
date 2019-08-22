/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.skyscreamer.jsonassert;

import static org.junit.jupiter.api.Assertions.fail;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT_ORDER;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

/**
 * Unit tests for {@link JSONAssert}
 */
class JSONAssertTest {
    @Test
    void testString() throws JSONException {
        testPass("\"Joe\"", "\"Joe\"", STRICT);
        testPass("\"Joe\"", "\"Joe\"", LENIENT);
        testPass("\"Joe\"", "\"Joe\"", NON_EXTENSIBLE);
        testPass("\"Joe\"", "\"Joe\"", STRICT_ORDER);
        testFail("\"Joe\"", "\"Joe1\"", STRICT);
        testFail("\"Joe\"", "\"Joe2\"", LENIENT);
        testFail("\"Joe\"", "\"Joe3\"", NON_EXTENSIBLE);
        testFail("\"Joe\"", "\"Joe4\"", STRICT_ORDER);
    }

    @Test
    void testNumber() throws JSONException {
        testPass("123", "123", STRICT);
        testPass("123", "123", LENIENT);
        testPass("123", "123", NON_EXTENSIBLE);
        testPass("123", "123", STRICT_ORDER);
        testFail("123", "1231", STRICT);
        testFail("123", "1232", LENIENT);
        testFail("123", "1233", NON_EXTENSIBLE);
        testFail("123", "1234", STRICT_ORDER);
        testPass("0", "0", STRICT);
        testPass("-1", "-1", STRICT);
        testPass("0.1", "0.1", STRICT);
        testPass("1.2e5", "1.2e5", STRICT);
        testPass("20.4e-1", "20.4e-1", STRICT);
        testFail("310.1e-1", "31.01", STRICT); // should fail though numbers are the same?
    }

    @Test
    void testSimple() throws JSONException {
        testPass("{id:1}", "{id:1}", STRICT);
        testFail("{id:1}", "{id:2}", STRICT);
        testPass("{id:1}", "{id:1}", LENIENT);
        testFail("{id:1}", "{id:2}", LENIENT);
        testPass("{id:1}", "{id:1}", NON_EXTENSIBLE);
        testFail("{id:1}", "{id:2}", NON_EXTENSIBLE);
        testPass("{id:1}", "{id:1}", STRICT_ORDER);
        testFail("{id:1}", "{id:2}", STRICT_ORDER);
    }

    @Test
    void testSimpleStrict() throws JSONException {
        testPass("{id:1}", "{id:1,name:\"Joe\"}", LENIENT);
        testFail("{id:1}", "{id:1,name:\"Joe\"}", STRICT);
        testPass("{id:1}", "{id:1,name:\"Joe\"}", STRICT_ORDER);
        testFail("{id:1}", "{id:1,name:\"Joe\"}", NON_EXTENSIBLE);
    }

    @Test
    void testReversed() throws JSONException {
        testPass("{name:\"Joe\",id:1}", "{id:1,name:\"Joe\"}", LENIENT);
        testPass("{name:\"Joe\",id:1}", "{id:1,name:\"Joe\"}", STRICT);
        testPass("{name:\"Joe\",id:1}", "{id:1,name:\"Joe\"}", NON_EXTENSIBLE);
        testPass("{name:\"Joe\",id:1}", "{id:1,name:\"Joe\"}", STRICT_ORDER);
    }

    @Test // Currently JSONAssert assumes JSONObject.
    void testArray() throws JSONException {
        testPass("[1,2,3]","[1,2,3]", STRICT);
        testPass("[1,2,3]","[1,3,2]", LENIENT);
        testFail("[1,2,3]","[1,3,2]", STRICT);
        testFail("[1,2,3]","[4,5,6]", LENIENT);
        testPass("[1,2,3]","[1,2,3]", STRICT_ORDER);
        testPass("[1,2,3]","[1,3,2]", NON_EXTENSIBLE);
        testFail("[1,2,3]","[1,3,2]", STRICT_ORDER);
        testFail("[1,2,3]","[4,5,6]", NON_EXTENSIBLE);
    }

    @Test
    void testNested() throws JSONException {
        testPass("{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Houston\", state:\"TX\"}}",
                "{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Houston\", state:\"TX\"}}", STRICT);
        testFail("{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Houston\", state:\"TX\"}}",
                "{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Austin\", state:\"TX\"}}", STRICT);
    }

    @Test
    void testVeryNested() throws JSONException {
        testPass("{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{p:\"blah\"}}}}}}}}}}}}}}}}",
                "{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{p:\"blah\"}}}}}}}}}}}}}}}}", STRICT);
        testFail("{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{p:\"blah\"}}}}}}}}}}}}}}}}",
                "{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{z:\"blah\"}}}}}}}}}}}}}}}}", STRICT);
    }

    @Test
    void testSimpleArray() throws JSONException {
        testPass("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Exact to exact (strict)
                "{id:1,pets:[\"dog\",\"cat\",\"fish\"]}",
                STRICT);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Out-of-order fails (strict)
                "{id:1,pets:[\"dog\",\"fish\",\"cat\"]}",
                STRICT);
        testPass("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Out-of-order ok
                "{id:1,pets:[\"dog\",\"fish\",\"cat\"]}",
                LENIENT);
        testPass("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Out-of-order ok
                "{id:1,pets:[\"dog\",\"fish\",\"cat\"]}",
                NON_EXTENSIBLE);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Out-of-order fails (strict order)
                "{id:1,pets:[\"dog\",\"fish\",\"cat\"]}",
                STRICT_ORDER);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Mismatch
                "{id:1,pets:[\"dog\",\"cat\",\"bird\"]}",
                STRICT);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Mismatch
                "{id:1,pets:[\"dog\",\"cat\",\"bird\"]}",
                LENIENT);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Mismatch
                "{id:1,pets:[\"dog\",\"cat\",\"bird\"]}",
                STRICT_ORDER);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Mismatch
                "{id:1,pets:[\"dog\",\"cat\",\"bird\"]}",
                NON_EXTENSIBLE);
    }

    @Test
    void testSimpleMixedArray() throws JSONException {
        testPass("{stuff:[321, \"abc\"]}", "{stuff:[\"abc\", 321]}", LENIENT);
        testFail("{stuff:[321, \"abc\"]}", "{stuff:[\"abc\", 789]}", LENIENT);
    }

    @Test
    void testComplexMixedStrictArray() throws JSONException {
        testPass("{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", "{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", STRICT);
    }

    @Test
    void testComplexMixedArray() throws JSONException {
        testPass("{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", "{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", LENIENT);
    }

    @Test
    void testComplexArrayNoUniqueID() throws JSONException {
        testPass("{stuff:[{address:{addr1:\"123 Main\"}}, {address:{addr1:\"234 Broad\"}}]}",
                "{stuff:[{address:{addr1:\"123 Main\"}}, {address:{addr1:\"234 Broad\"}}]}",
                LENIENT);
    }

    @Test
    void testSimpleAndComplexStrictArray() throws JSONException {
        testPass("{stuff:[123,{a:\"b\"}]}", "{stuff:[123,{a:\"b\"}]}", STRICT);
    }

    @Test
    void testSimpleAndComplexArray() throws JSONException {
        testPass("{stuff:[123,{a:\"b\"}]}", "{stuff:[123,{a:\"b\"}]}", LENIENT);
    }

    @Test
    void testComplexArray() throws JSONException {
        testPass("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                 "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                 STRICT); // Exact to exact (strict)
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                STRICT); // Out-of-order fails (strict)
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                STRICT_ORDER); // Out-of-order fails (strict order)
        testPass("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                LENIENT); // Out-of-order ok
        testPass("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                NON_EXTENSIBLE); // Out-of-order ok
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                STRICT); // Mismatch (strict)
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                LENIENT); // Mismatch
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                STRICT_ORDER); // Mismatch
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                NON_EXTENSIBLE); // Mismatch
    }

    @Test
    void testArrayOfArraysStrict() throws JSONException {
        testPass("{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", "{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", STRICT);
        testFail("{id:1,stuff:[[1,2],[2,3],[3,4],[]]}", "{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", STRICT);
    }

    @Test
    void testArrayOfArrays() throws JSONException {
        testPass("{id:1,stuff:[[4,3],[3,2],[],[1,2]]}", "{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", LENIENT);
    }
    
    @Test
    void testLenientArrayRecursion() throws JSONException {
        testPass("[{\"arr\":[5, 2, 1]}]", "[{\"b\":3, \"arr\":[1, 5, 2]}]", LENIENT);
    }
   
    @Test 
    void testFieldMismatch() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{name:\"Pat\"}", "{name:\"Sue\"}", STRICT);
        FieldComparisonFailure comparisonFailure = result.getFieldFailures().iterator().next();
        Assertions.assertEquals("Pat", comparisonFailure.getExpected());
        Assertions.assertEquals("Sue", comparisonFailure.getActual());
        Assertions.assertEquals("name", comparisonFailure.getField());
    }

    @Test
    void testBooleanArray() throws JSONException {
        testPass("[true, false, true, true, false]", "[true, false, true, true, false]", STRICT);
        testPass("[false, true, true, false, true]", "[true, false, true, true, false]", LENIENT);
        testFail("[false, true, true, false, true]", "[true, false, true, true, false]", STRICT);
        testPass("[false, true, true, false, true]", "[true, false, true, true, false]", NON_EXTENSIBLE);
        testFail("[false, true, true, false, true]", "[true, false, true, true, false]", STRICT_ORDER);
    }

    @Test
    void testNullProperty() throws JSONException {
        testFail("{id:1,name:\"Joe\"}", "{id:1,name:null}", STRICT);
        testFail("{id:1,name:null}", "{id:1,name:\"Joe\"}", STRICT);
    }

    @Test
    void testIncorrectTypes() throws JSONException {
        testFail("{id:1,name:\"Joe\"}", "{id:1,name:[]}", STRICT);
        testFail("{id:1,name:[]}", "{id:1,name:\"Joe\"}", STRICT);
    }

    @Test
    void testNullEquality() throws JSONException {
        testPass("{id:1,name:null}", "{id:1,name:null}", STRICT);
    }

    @Test
    void testExpectedArrayButActualObject() throws JSONException {
        testFail("[1]", "{id:1}", LENIENT);
    }

    @Test
    void testExpectedObjectButActualArray() throws JSONException {
        testFail("{id:1}", "[1]", LENIENT);
    }

    @Test
    void testEquivalentIntAndLong() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Long.valueOf(12345));
        JSONAssert.assertEquals(expected, actual, true);
        JSONAssert.assertEquals(actual, expected, true);
    }

    @Test
    void testEquivalentIntAndDouble() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12345.0));
        JSONAssert.assertEquals(expected, actual, true);
        JSONAssert.assertEquals(actual, expected, true);
    }

    @Test
    void testAssertNotEqualsWhenEqualStrict() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12345));
        Assertions.assertThrows(AssertionError.class, () -> JSONAssert.assertNotEquals(expected, actual, true));
    }

    @Test
    void testAssertNotEqualsWhenEqualLenient() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12345));
        Assertions.assertThrows(AssertionError.class, () -> JSONAssert.assertNotEquals(expected, actual, false));
    }

    @Test()
    void testAssertNotEqualsWhenEqualDiffObjectsStrict() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        expected.put("name", "Joe");
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertNotEquals(expected, actual, true);
    }

    @Test
    void testAssertNotEqualsWhenEqualDiffObjectsLenient() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        expected.put("name", "Joe");
        actual.put("name", "Joe");
        actual.put("id", Double.valueOf(12345));
        Assertions.assertThrows(AssertionError.class, () -> JSONAssert.assertNotEquals(expected, actual, false));
    }

    @Test()
    void testAssertNotEqualsWhenDifferentStrict() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12346));
        JSONAssert.assertNotEquals(expected, actual, true);
    }

    @Test()
    void testAssertNotEqualsWhenDifferentLenient() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12346));
        JSONAssert.assertNotEquals(expected, actual, false);
    }

    @Test()
    void testAssertNotEqualsString() throws JSONException {
        JSONAssert.assertNotEquals("[1,2,3]", "[1,3,2]", STRICT);
        JSONAssert.assertNotEquals("[1,2,3]", "[1,2,4]", LENIENT);
        JSONAssert.assertNotEquals("[1,2,3]", "[1,3,2]", true);
        JSONAssert.assertNotEquals("[1,2,3]", "[1,2,4]", false);
    }
    
    @Test()
    void testAssertEqualsString() throws JSONException {
        JSONAssert.assertEquals("[1,2,3]", "[1,2,3]", true);
        JSONAssert.assertEquals("{id:12345}", "{id:12345}", false);
        JSONAssert.assertEquals("{id:12345}", "{id:12345, name:\"john\"}", LENIENT);
        JSONAssert.assertEquals("{id:12345}", "{id:12345}", LENIENT);
        JSONAssert.assertEquals("{id:12345}", "{id:12345, name:\"john\"}", LENIENT);
    }

    @Test()
    void testAssertNotEqualsStringAndJSONObject() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertEquals("{id:12345}", actual, false);
        JSONAssert.assertNotEquals("{id:12346}", actual, false);
    }

    @Test()
    void testAssertNotEqualsJSONArray() throws JSONException {
        JSONArray actual = new JSONArray(Arrays.asList(1, 2, 3));
        JSONAssert.assertEquals("[1,2,3]", actual, false);
        JSONAssert.assertNotEquals("[1,2,4]", actual, false);
        JSONAssert.assertNotEquals("[1,3,2]", actual, true);
        JSONAssert.assertNotEquals(new JSONArray(Arrays.asList(1, 2, 4)), actual, false);
        JSONAssert.assertNotEquals(new JSONArray(Arrays.asList(1, 3, 2)), actual, true);
    }
    
    @Test
    void testAssertEqualsStringJSONArrayBooleanWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(Arrays.asList(1, 2, 3));
        JSONAssert.assertEquals("Message", "[1,2,3]", actual, false);
        performAssertEqualsTestForMessageVerification("[1,2,4]", actual, false);
        performAssertEqualsTestForMessageVerification("[1,3,2]", actual, true);
    }
    
    @Test
    void testAssertEqualsStringJSONArrayCompareModeWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(Arrays.asList(1, 2, 3));
        JSONAssert.assertEquals("Message", "[1,2,3]", actual, LENIENT);
        performAssertEqualsTestForMessageVerification("[1,2,4]", actual, LENIENT);
        performAssertEqualsTestForMessageVerification("[1,3,2]", actual, STRICT);
    }

    @Test
    void testAssertEqualsJSONArray2BooleanWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(Arrays.asList(1, 2, 3));
        JSONAssert.assertEquals("Message", new JSONArray(Arrays.asList(1, 2, 3)), actual, false);
        performAssertEqualsTestForMessageVerification(new JSONArray(Arrays.asList(1, 2, 4)), actual, false);
        performAssertEqualsTestForMessageVerification(new JSONArray(Arrays.asList(1, 3, 2)), actual, true);
    }
    
    @Test
    void testAssertEqualsJSONArray2JSONCompareWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(Arrays.asList(1, 2, 3));
        
        JSONAssert.assertEquals("Message", new JSONArray(Arrays.asList(1, 2, 3)), actual, LENIENT);
        performAssertEqualsTestForMessageVerification(new JSONArray(Arrays.asList(1, 2, 4)), actual, LENIENT);
        performAssertEqualsTestForMessageVerification(new JSONArray(Arrays.asList(1, 3, 2)), actual, STRICT);
    }
    
    @Test
    void testAssertEqualsString2Boolean() throws JSONException {
        JSONAssert.assertEquals("Message", "{id:12345}", "{id:12345}", false);
        JSONAssert.assertEquals("Message", "{id:12345}", "{id:12345, name:\"john\"}", false);
        
        performAssertEqualsTestForMessageVerification("{id:12345}", "{id:12345, name:\"john\"}", true);
        performAssertEqualsTestForMessageVerification("{id:12345}", "{id:123456}", false);
    }
    
    @Test
    void testAssertEqualsString2JSONCompare() throws JSONException {
        JSONAssert.assertEquals("Message", "{id:12345}", "{id:12345}", LENIENT);
        JSONAssert.assertEquals("Message", "{id:12345}", "{id:12345, name:\"john\"}", LENIENT);
        
        performAssertEqualsTestForMessageVerification("{id:12345}", "{id:12345, name:\"john\"}", STRICT);
        performAssertEqualsTestForMessageVerification("{id:12345}", "{id:123456}", LENIENT);
    }
    
    @Test
    void testAssertEqualsStringJSONObjectBoolean() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertEquals("Message", "{id:12345}", actual, false);
        performAssertEqualsTestForMessageVerification("{id:12346}", actual, false);
        performAssertEqualsTestForMessageVerification("[1,2,3]", "[1,3,2]", true);
    }
    
    @Test
    void testAssertEqualsStringJSONObjectJSONCompare() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertEquals("Message", "{id:12345}", actual, LENIENT);
        performAssertEqualsTestForMessageVerification("{id:12346}", actual, LENIENT);
        performAssertEqualsTestForMessageVerification("[1,2,3]", "[1,3,2]", STRICT);
    }
    
    @Test
    void testAssertEqualsJSONObject2JSONCompare() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("name", "Joe");
        actual.put("id", Integer.valueOf(12345));
        JSONAssert.assertEquals("Message", expected, actual, LENIENT);
        
        expected.put("street", "St. Paul");
        performAssertEqualsTestForMessageVerification(expected, actual, LENIENT);
        
        expected = new JSONObject();
        actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12346));
        performAssertEqualsTestForMessageVerification(expected, actual, STRICT);
    }
    
    @Test
    void testAssertEqualsJSONObject2Boolean() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("name", "Joe");
        actual.put("id", Integer.valueOf(12345));
        JSONAssert.assertEquals("Message", expected, actual, false);
        
        expected.put("street", "St. Paul");
        performAssertEqualsTestForMessageVerification(expected, actual, false);
        
        expected = new JSONObject();
        actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12346));
        performAssertEqualsTestForMessageVerification(expected, actual, true);
    }
    
    @Test
    void testAssertEqualsString2JsonComparator() throws IllegalArgumentException, JSONException {
        JSONAssert.assertEquals("Message", "{\"entry\":{\"id\":x}}", "{\"entry\":{\"id\":1, \"id\":2}}", 
            new CustomComparator(
                JSONCompareMode.STRICT, 
                new Customization("entry.id", 
                new RegularExpressionValueMatcher<Object>("\\d"))
         ));
        
        performAssertEqualsTestForMessageVerification("{\"entry\":{\"id\":x}}", "{\"entry\":{\"id\":1, \"id\":as}}", 
            new CustomComparator(
                JSONCompareMode.STRICT, 
                new Customization("entry.id", 
                new RegularExpressionValueMatcher<Object>("\\d"))
        ));
    }
    
    @Test
    void testAssertNotEqualsStringJSONArrayBooleanWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(Arrays.asList(1, 2, 3));
        JSONAssert.assertNotEquals("Message", "[1,4,3]", actual, false);
        JSONAssert.assertNotEquals("Message", "[1,4,3]", actual, true);
        performAssertNotEqualsTestForMessageVerification("[1,3,2]", actual, false);
        performAssertNotEqualsTestForMessageVerification("[1,2,3]", actual, true);
    }
    
    @Test
    void testAssertNotEqualsStringJSONArrayCompareModeWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(Arrays.asList(1, 2, 3));
        JSONAssert.assertNotEquals("Message", "[1,2,4]", actual, LENIENT);
        JSONAssert.assertNotEquals("Message", "[1,2,4]", actual, STRICT);
        performAssertNotEqualsTestForMessageVerification("[1,3,2]", actual, LENIENT);
        performAssertNotEqualsTestForMessageVerification("[1,2,3]", actual, STRICT);
    }
    
    @Test
    void testAssertNotEqualsJSONArray2BooleanWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(Arrays.asList(1, 2, 3));
        JSONAssert.assertNotEquals("Message", new JSONArray(Arrays.asList(1, 4, 3)), actual, false);
        performAssertNotEqualsTestForMessageVerification(new JSONArray(Arrays.asList(1, 3, 2)), actual, false);
        performAssertNotEqualsTestForMessageVerification(new JSONArray(Arrays.asList(1, 2, 3)), actual, true);
    }
    
    @Test
    void testAssertNotEqualsJSONArray2JSONCompareWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(Arrays.asList(1, 2, 3));
        
        JSONAssert.assertNotEquals("Message", new JSONArray(Arrays.asList(1, 4, 3)), actual, LENIENT);
        performAssertNotEqualsTestForMessageVerification(new JSONArray(Arrays.asList(1, 3, 2)), actual, LENIENT);
        performAssertNotEqualsTestForMessageVerification(new JSONArray(Arrays.asList(1, 2, 3)), actual, STRICT);
    }
    
    @Test
    void testAssertNotEqualsString2Boolean() throws JSONException {
        JSONAssert.assertNotEquals("Message", "{id:12345}", "{id:45}", false);
        JSONAssert.assertNotEquals("Message", "{id:12345}", "{id:345, name:\"john\"}", false);
        
        performAssertNotEqualsTestForMessageVerification("{id:12345}", "{id:12345}", true);
        performAssertNotEqualsTestForMessageVerification("{id:12345}", "{id:12345, name:\"John\"}", false);
    }
    
    @Test
    void testAssertNotEqualsString2JSONCompare() throws JSONException {
        JSONAssert.assertNotEquals("Message", "{id:12345}", "{id:123}", LENIENT);
        JSONAssert.assertNotEquals("Message", "{id:12345, name:\"John\"}", "{id:12345}", LENIENT);
        
        performAssertNotEqualsTestForMessageVerification("{id:12345}", "{id:12345, name:\"john\"}", LENIENT);
        performAssertNotEqualsTestForMessageVerification("{id:12345}", "{id:12345}", STRICT);
    }
    
    @Test
    void testAssertNotEqualsStringJSONObjectBoolean() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertNotEquals("Message", "{id:1234}", actual, false);
        performAssertNotEqualsTestForMessageVerification("{id:12345}", actual, false);
        performAssertNotEqualsTestForMessageVerification("[1,2,3]", "[1,2,3]", true);
    }
    
    @Test
    void testAssertNotEqualsStringJSONObjectJSONCompare() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertNotEquals("Message", "{id:1234}", actual, LENIENT);
        performAssertNotEqualsTestForMessageVerification("{id:12345}", actual, LENIENT);
        performAssertNotEqualsTestForMessageVerification("[1,2,3]", "[1,2,3]", STRICT);
    }
    
    @Test
    void testAssertNtEqualsJSONObject2JSONCompare() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("name", "Joe");
        actual.put("id", Integer.valueOf(123));
        JSONAssert.assertNotEquals("Message", expected, actual, LENIENT);
        
        actual.remove("id");
        actual.put("id", Integer.valueOf(12345));
        performAssertNotEqualsTestForMessageVerification(expected, actual, LENIENT);
        
        expected = new JSONObject();
        actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12345));
        performAssertNotEqualsTestForMessageVerification(expected, actual, STRICT);
    }
    
    @Test
    void testAssertNotEqualsJSONObject2Boolean() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("name", "Joe");
        actual.put("id", Integer.valueOf(123));
        JSONAssert.assertNotEquals("Message", expected, actual, false);
        
        actual.remove("id");
        actual.put("id", Integer.valueOf(12345));
        performAssertNotEqualsTestForMessageVerification(expected, actual, false);
        
        expected = new JSONObject();
        actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12345));
        performAssertNotEqualsTestForMessageVerification(expected, actual, true);
    }
    
    @Test
    @Disabled
    void testAssertNotEqualsString2JsonComparator() throws IllegalArgumentException, JSONException {
        JSONAssert.assertNotEquals("Message", "{\"entry\":{\"id\":x}}", "{\"entry\":{\"id\":1, \"id\":hh}}", 
            new CustomComparator(
                JSONCompareMode.STRICT, 
                new Customization("entry.id", 
                new RegularExpressionValueMatcher<Object>("\\d"))
         ));
        
        performAssertNotEqualsTestForMessageVerification("{\"entry\":{\"id\":x}}", "{\"entry\":{\"id\":1, \"id\":2}}", 
            new CustomComparator(
                JSONCompareMode.STRICT, 
                new Customization("entry.id", 
                new RegularExpressionValueMatcher<Object>("\\d"))
        ));
    }
    
    private void testPass(String expected, String actual, JSONCompareMode compareMode)
            throws JSONException
    {
        String message = expected + " == " + actual + " (" + compareMode + ")";
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, compareMode);
        Assertions.assertTrue(result.passed(), message + "\n  " + result.getMessage());
    }

    private void testFail(String expected, String actual, JSONCompareMode compareMode)
            throws JSONException
    {
        String message = expected + " != " + actual + " (" + compareMode + ")";
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, compareMode);
        Assertions.assertTrue(result.failed(), message);
    }
    
    private void performAssertEqualsTestForMessageVerification(
        Object expected, 
        Object actual, 
        Object strictMode) throws JSONException {
        
        String message = "Message";
        String testShouldFailMessage = "The test should fail so that the message in AssertionError could be verified.";
        String strictModeMessage = "strictMode must be an instance of JSONCompareMode or Boolean";
        boolean assertEqualsFailed = true;
        if(expected instanceof String && actual instanceof String && strictMode instanceof JSONComparator) {
            try {
                JSONAssert.assertEquals(message, (String) expected, (String) actual, (JSONComparator) strictMode);
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        }
        else if(expected instanceof String && actual instanceof JSONArray) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertEquals(message, (String) expected, (JSONArray) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertEquals(message, (String) expected, (JSONArray) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof JSONArray && actual instanceof JSONArray) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertEquals(message, (JSONArray) expected, (JSONArray) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertEquals(message, (JSONArray) expected, (JSONArray) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof String && actual instanceof String) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertEquals(message, (String) expected, (String) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertEquals(message, (String) expected, (String) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof String && actual instanceof JSONObject) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertEquals(message, (String) expected, (JSONObject) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertEquals(message, (String) expected, (JSONObject) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof JSONObject && actual instanceof JSONObject) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertEquals(message, (JSONObject) expected, (JSONObject) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertEquals(message, (JSONObject) expected, (JSONObject) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else {
            fail("No overloaded method found to call");
        }
    }
    
    private void performAssertNotEqualsTestForMessageVerification(
        Object expected, 
        Object actual, 
        Object strictMode) 
        throws JSONException {
        
        String message = "Message";
        String testShouldFailMessage = "The test should fail so that the message in AssertionError could be verified.";
        String strictModeMessage = "strictMode must be an instance of JSONCompareMode or Boolean";
        boolean assertEqualsFailed = true;
        if(expected instanceof String && actual instanceof String && strictMode instanceof JSONComparator) {
            try {
                JSONAssert.assertNotEquals(message, (String) expected, (String) actual, (JSONComparator) strictMode);
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        }
        else if(expected instanceof String && actual instanceof JSONArray) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertNotEquals(message, (String) expected, (JSONArray) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertNotEquals(message, (String) expected, (JSONArray) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof JSONArray && actual instanceof JSONArray) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertNotEquals(message, (JSONArray) expected, (JSONArray) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertNotEquals(message, (JSONArray) expected, (JSONArray) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof String && actual instanceof String) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertNotEquals(message, (String) expected, (String) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertNotEquals(message, (String) expected, (String) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof String && actual instanceof JSONObject) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertNotEquals(message, (String) expected, (JSONObject) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertNotEquals(message, (String) expected, (JSONObject) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof JSONObject && actual instanceof JSONObject) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertNotEquals(message, (JSONObject) expected, (JSONObject) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertNotEquals(message, (JSONObject) expected, (JSONObject) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else {
            fail("No overloaded method found to call");
        }
    }

    private void handleAssertionError(String message, boolean assertEqualsFailed, AssertionError ae) throws AssertionError {
        if(assertEqualsFailed) {
            verifyErrorMessage(message, ae);
        } else {
            throw ae;
        }
    }
    
    private void verifyErrorMessage(String message, AssertionError ae) {
        Assertions.assertTrue(ae.getMessage().contains(message));
        Assertions.assertTrue(ae.getMessage().startsWith(message));
    }
}
