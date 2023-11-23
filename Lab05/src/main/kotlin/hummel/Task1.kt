package hummel

import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

fun launchTask1() {
	val queue = ConcurrentLinkedQueue<() -> Unit>()

	val threads = mutableListOf<Thread>()
	repeat(10) { threads.add(thread { queue.add { println("task from thread $it") } }) }
	threads.forEach { it.join() }

	while (!queue.isEmpty()) {
		print("Executing ")
		queue.poll().invoke()
	}
}