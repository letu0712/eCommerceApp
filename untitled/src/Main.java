import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Long> sumDigPow(long a, long b){
        List<Long> result = new ArrayList<>();
        for(long i=a; i<=b; i++){
            List<Character> listDigit = new ArrayList<>();
            String temp = String.valueOf(i);
            for(int k=0; k<temp.length(); k++){
                listDigit.add((temp.charAt(k)));
            }
            double sum=0L;
            for(int j=0; j<listDigit.size(); j++){
                sum+= Math.pow(Long.parseLong(listDigit.get(j).toString()), j+1);
            }
            if((long)sum == i) {
                result.add(i);
            }
        }
        return result;
    }
    public static void main(String[] args) {
        System.out.println(sumDigPow(1, 100));
    }
}
