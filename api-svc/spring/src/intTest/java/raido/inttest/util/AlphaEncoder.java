package raido.inttest.util;

import raido.apisvc.util.Guard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 Based off https://stackoverflow.com/a/22043836/924597
 */
public enum AlphaEncoder {
  Base_64("-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz"),
  Base_36("0123456789abcdefghijklmnopqrstuvwxyz"),
  Base_26("abcdefghijklmnopqrstuvwxyz");

  AlphaEncoder(String alphabet) {
    Guard.notNull(alphabet);
    Guard.hasValue(alphabet);
    Guard.isTrue(alphabet.length() > 1);
    this.alphabet = alphabet.toCharArray();
    this.alphabetLength = this.alphabet.length;
    // The binarySearch() inside decode() depends on the array being ordered.
    // I already put them in order in order in the assignment, this just
    // makes double sure
    Arrays.sort(this.alphabet);
  }

  private char[] alphabet;
  private int alphabetLength;

  public String encode(long victim) {
    final List<Character> list = new ArrayList<>();

    do{
      long mod = victim % this.alphabetLength;
      list.add(this.alphabet[(int) mod]);
      victim /= this.alphabetLength;
    } while( victim > 0 );

    // why?
    Collections.reverse(list);

    return list.stream().map(String::valueOf).collect(Collectors.joining());
  }

  public long decode(final String encoded) {
    Guard.hasValue("can't decode empty or whitespace string", encoded);
    long ret = 0;
    char c;
    for( int index = 0; index < encoded.length(); index++ ){
      c = encoded.charAt(index);
      ret *= this.alphabetLength;
      ret += Arrays.binarySearch(this.alphabet, c);
    }
    return ret;
  }
}