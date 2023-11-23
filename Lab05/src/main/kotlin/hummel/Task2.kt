package hummel

import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

fun launchTask2() {
	val tasks = 10
	val executors = 3

	val queue = ConcurrentLinkedQueue<() -> Unit>()

	val threads = mutableListOf<Thread>()
	repeat(tasks) { threads.add(thread { queue.add { println("task from thread $it") } }) }
	threads.forEach { it.join() }

	val avg = tasks / executors
	val rest = tasks % executors
	val strategy = IntArray(executors) { if (it == executors - 1) avg + rest else avg }

	repeat(executors) {
		thread {
			repeat(strategy[it]) {
				print("Thread $it: executing ")
				queue.poll().invoke()
			}
		}.join()
	}
}