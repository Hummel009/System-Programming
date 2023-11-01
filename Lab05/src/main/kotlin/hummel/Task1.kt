package hummel

import java.util.concurrent.ConcurrentLinkedQueue

fun main() {
	val taskQueue = ConcurrentLinkedQueue<Runnable>()

	val insertThread1 = Thread {
		for (i in 1..5) {
			val task = { println("задание $i из потока 1") }
			taskQueue.add(task)
		}
	}

	val insertThread2 = Thread {
		for (i in 6..10) {
			val task = { println("задание $i из потока 2") }
			taskQueue.add(task)
		}
	}

	insertThread1.start()
	insertThread2.start()
	insertThread1.join()
	insertThread2.join()

	while (!taskQueue.isEmpty()) {
		val task = taskQueue.poll()
		task?.let {
			print("Выполняю ")
			it.run()
			Thread.sleep(500)
		}
	}
}