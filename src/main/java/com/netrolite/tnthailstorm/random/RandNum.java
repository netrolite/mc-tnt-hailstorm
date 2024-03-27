package com.netrolite.tnthailstorm.random;

import java.util.Random;

public class RandNum {
  Random rand = new Random();

  public int genInt(int from, int to) {
    return rand.nextInt(from, to + 1);
  }

  public float genFloat(float from, float to) {
    return rand.nextFloat(from, to + 1);
  }
}
