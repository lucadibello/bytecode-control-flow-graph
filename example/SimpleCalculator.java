public class SimpleCalculator {

  public int max(int a, int b) {
    if (a > b) {
      return a;
    } else {
      return b;
    }
  }

  public int factorial(int n) {
    int result = 1;
    while (n > 1) {
      result *= n;
      n--;
    }
    return result;
  }
}
