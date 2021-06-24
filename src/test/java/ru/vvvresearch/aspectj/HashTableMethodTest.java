package ru.vvvresearch.aspectj;

import oracle.xdo.common.util.HashTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.vvvresearch.SyncHashTableAspect;

import java.util.concurrent.atomic.AtomicInteger;

class HashTableMethodTest {
	public static final int NUM_OF_THREADS = 350;
	public static final int TRY_COUNT = 30;
	public static final int MS_TIME_OUT = 2000;//таймаут в миллисекундах
	private static AtomicInteger count = new AtomicInteger(NUM_OF_THREADS);
	private static final int[][] mMapArray = new int[][]{{32, 32}, {33, 33}, {34, 8704}, {35, 35}, {36, 8707}, {37, 37}, {38, 38}, {39, 8717}, {40, 40}, {41, 41}, {42, 8727}, {43, 43}, {44, 44}, {45, 8722}, {46, 46}, {47, 47}, {48, 48}, {49, 49}, {50, 50}, {51, 51}, {52, 52}, {53, 53}, {54, 54}, {55, 55}, {56, 56}, {57, 57}, {58, 58}, {59, 59}, {60, 60}, {61, 61}, {62, 62}, {63, 63}, {64, 8773}, {65, 913}, {66, 914}, {67, 935}, {68, 916}, {69, 917}, {70, 934}, {71, 915}, {72, 919}, {73, 921}, {74, 977}, {75, 922}, {76, 923}, {77, 924}, {78, 925}, {79, 927}, {80, 928}, {81, 920}, {82, 929}, {83, 931}, {84, 932}, {85, 933}, {86, 962}, {87, 937}, {88, 926}, {89, 936}, {90, 918}, {91, 91}, {92, 8756}, {93, 93}, {94, 8869}, {95, 95}, {96, 63717}, {97, 945}, {98, 946}, {99, 967}, {100, 948}, {101, 949}, {102, 966}, {103, 947}, {104, 951}, {105, 953}, {106, 981}, {107, 954}, {108, 955}, {109, 956}, {110, 957}, {111, 959}, {112, 960}, {113, 952}, {114, 961}, {115, 963}, {116, 964}, {117, 965}, {118, 982}, {119, 969}, {120, 958}, {121, 968}, {122, 950}, {123, 123}, {124, 124}, {125, 125}, {126, 8764}, {160, 8364}, {161, 978}, {162, 8242}, {163, 8804}, {164, 8260}, {165, 8734}, {166, 402}, {167, 9827}, {168, 9830}, {169, 9829}, {170, 9824}, {171, 8596}, {172, 8592}, {173, 8593}, {174, 8594}, {175, 8595}, {176, 176}, {177, 177}, {178, 8243}, {179, 8805}, {180, 215}, {181, 8733}, {182, 8706}, {183, 8226}, {184, 247}, {185, 8800}, {186, 8801}, {187, 8776}, {188, 8230}, {189, 9168}, {190, 9135}, {191, 8629}, {192, 8501}, {193, 8465}, {194, 8476}, {195, 8472}, {196, 8855}, {197, 8853}, {198, 8709}, {199, 8745}, {200, 8746}, {201, 8835}, {202, 8839}, {203, 8836}, {204, 8834}, {205, 8838}, {206, 8712}, {207, 8713}, {208, 8736}, {209, 8711}, {210, 174}, {211, 169}, {212, 8482}, {213, 8719}, {214, 8730}, {215, 8901}, {216, 172}, {217, 8743}, {218, 8744}, {219, 8660}, {220, 8656}, {221, 8657}, {222, 8658}, {223, 8659}, {224, 9674}, {225, 12296}, {226, 174}, {227, 169}, {228, 8482}, {229, 8721}, {230, 9115}, {231, 9116}, {232, 9117}, {233, 9121}, {234, 9122}, {235, 9123}, {236, 9127}, {237, 9128}, {238, 9129}, {239, 9130}, {240, 63743}, {241, 12297}, {242, 8747}, {243, 8992}, {244, 9134}, {245, 8993}, {246, 9118}, {247, 9119}, {248, 9120}, {249, 9124}, {250, 9125}, {251, 9126}, {252, 9131}, {253, 9132}, {254, 9133}};
	static HashTable mMap = new HashTable();

	@Test
	void xdoCoreHashTableWithoutSync() throws InterruptedException {
		SyncHashTableAspect.setIsSync(false);
		for (int ii = 0; ii < TRY_COUNT; ii++) {
			count.set(NUM_OF_THREADS);
			mMap = new HashTable();
			doHashTableInParallel();
			doTimeOut(ii, 2000);
			if (count.get() != 0) {
				System.out.println("Завис на попытке " + ii);
				Assertions.assertTrue(true, "Завис на попытке " + ii);
				return;
			}
		}
		Assertions.fail("Не завис за " + TRY_COUNT + " попыток");
	}

	@Test
	void xdoCoreHashTableWithSync() throws InterruptedException {
		SyncHashTableAspect.setIsSync(true);
		for (int ii = 0; ii < TRY_COUNT; ii++) {
			count.set(NUM_OF_THREADS);
			mMap = new HashTable();
			doHashTableInParallel();
			doTimeOut(ii, MS_TIME_OUT);
			if (count.get() != 0) {
				System.out.println("Завис на попытке " + ii);
				Assertions.fail("Завис на попытке " + ii);
				break;
			}
		}
		Assertions.assertTrue(true, "Не завис.");
	}

	private void doHashTableInParallel() {
		for (int t = 0; t < NUM_OF_THREADS; t++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean y = true;
					for (int i = 0; i < mMapArray.length; ++i) {
						int src = mMapArray[i][0];
						int dest = mMapArray[i][1];
						mMap.put(new Integer(src), new Integer(dest));
						try {
							if (y) {
								y = false;
								Thread.sleep(100);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					count.decrementAndGet();
//						System.out.println("Last Threads-" + count.decrementAndGet());
				}
			}).start();
		}
	}

	private void doTimeOut(int tryNum, int msTimeOut) throws InterruptedException {
		for (int j = 0; j < msTimeOut / 100; j++) {
			Thread.sleep(100);
			if (count.get() == 0) {
				System.out.println("Не завис на попытке " + tryNum);
				break;
			}
		}
	}


}