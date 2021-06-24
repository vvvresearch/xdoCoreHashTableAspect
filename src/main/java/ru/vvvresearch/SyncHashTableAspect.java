package ru.vvvresearch;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jetbrains.annotations.TestOnly;

/**
 * Аспект для реализации потокобезопасной версии oracle.xdo.common.util.HashTable
 *
 * @author Kabanets.viktor 23.06.2021   09:24
 */
@Aspect
public class SyncHashTableAspect {
	private static boolean isSync = true;
	private static final Object LOCK = new Object();

	@Pointcut("execution(public * oracle.xdo.common.util.HashTable.*(..))")
	public void callAt() {
		//pointcut
	}


	@Around("callAt()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {

		Object proceed;
		if (isSync) {
			synchronized (LOCK) {
				proceed = pjp.proceed();
			}
		} else {
			proceed = pjp.proceed();
		}
		return proceed;
	}

	@TestOnly
	public static void setIsSync(boolean isSync) {
		SyncHashTableAspect.isSync = isSync;
	}
}
