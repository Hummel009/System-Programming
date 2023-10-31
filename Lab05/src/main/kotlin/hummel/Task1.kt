package hummel

import java.util.concurrent.ConcurrentLinkedQueue

class TaskQueue {
	private val queue = ConcurrentLinkedQueue<Runnable>()

	fun enqueue(task: Runnable) {
		queue.add(task)
	}

	fun dequeue(): Runnable? {
		return queue.poll()
	}

	fun isEmpty(): Boolean {
		return queue.isEmpty()
	}
}

fun main() {
	val taskQueue = TaskQueue()

	val insertThread1 = Thread {
		for (i in 1..5) {
			val task = { println("задание $i из потока 1") }
			taskQueue.enqueue(task)
		}
	}

	val insertThread2 = Thread {
		for (i in 6..10) {
			val task = { println("задание $i из потока 2") }
			taskQueue.enqueue(task)
		}
	}

	// Поток для извлечения элементов
	val removeThread = Thread {
		while (true) {
			if (!taskQueue.isEmpty()) {
				val task = taskQueue.dequeue()
				task?.let {
					print("Выполняю ")
					it.run()
				}
			}
			Thread.sleep(500)
		}
	}

	insertThread1.start()
	insertThread2.start()
	insertThread1.join()
	insertThread2.join()
	removeThread.start()
	removeThread.join()
}