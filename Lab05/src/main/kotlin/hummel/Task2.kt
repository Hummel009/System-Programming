package hummel

import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

fun main() {
	val tasks = 10
	val executors = 3

	val queue = ConcurrentLinkedQueue<() -> Unit>()

	val threads = mutableListOf<Thread>()
	repeat(tasks) { threads.add(thread { queue.add { println("задание из потока $it") } }) }
	threads.forEach { it.join() }

	val avg = tasks / executors
	val rest = tasks % executors
	val strategy = IntArray(executors) { if (it == executors - 1) avg + rest else avg }

	repeat(executors) {
		thread {
			repeat(strategy[it]) {
				print("Поток $it: выполняю ")
				queue.poll().invoke()
			}
		}.join()
	}
}