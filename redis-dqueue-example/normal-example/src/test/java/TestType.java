import io.github.biezhi.redisdqueue.core.Callback;
import io.github.biezhi.redisdqueue.enums.ConsumeStatus;
import io.github.biezhi.redisdqueue.utils.ClassUtil;

/**
 * @author biezhi
 * @date 2019/11/25
 */
public class TestType {

	public static void main(String[] args) {
		Callback<String> callback = data -> {
			return ConsumeStatus.CONSUMED;
//			return ConsumeStatus.RETRY;
		};

		Class type = ClassUtil.getGenericType(callback);
		System.out.println(type);
	}

}
