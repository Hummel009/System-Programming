package hummel

import java.io.File
import java.util.Collections.sort
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

fun launchTask3() {
	val executors = 3
	var lists: List<List<String>>? = null

	val queue = ConcurrentLinkedQueue<() -> Unit>()
	thread {
		lists = File("input.txt").readLines().chunk(executors)
		repeat(executors) { queue.add { sort((lists ?: return@add)[it]) } }
	}.join()
	val threads = mutableListOf<Thread>()

	repeat(executors) { threads.add(thread { queue.poll().invoke() }) }
	threads.forEach { it.join() }

	thread { lists?.flatten()?.mergeSort()?.forEach { print("$it ") } }
}

private fun List<String>.chunk(executors: Int): List<List<String>> {
	return chunked(size / executors + if (size % executors == 0) 0 else 1)
}

private fun List<String>.mergeSort(): List<String> {
	fun mergeStrings(left: List<String>, right: List<String>): List<String> {
		return sequence {
			var leftIndex = 0
			var rightIndex = 0

			while (leftIndex < left.size && rightIndex < right.size) {
				yield(if (left[leftIndex] < right[rightIndex]) left[leftIndex++] else right[rightIndex++])
			}

			yieldAll(left.subList(leftIndex, left.size))
			yieldAll(right.subList(rightIndex, right.size))
		}.toList()
	}

	return if (size <= 1) this else {
		val middle = size / 2
		val (left, right) = subList(0, middle) to subList(middle, size)
		mergeStrings(left.mergeSort(), right.mergeSort())
	}
}