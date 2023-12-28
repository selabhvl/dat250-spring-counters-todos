package no.hvl.dat250.rest.counters;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Counter spring example.
 */
@RestController
public class CounterController {

	private Counters counters = new Counters(0, 0);

	@GetMapping("/counters")
	public Counters getTodos() {
		return counters;
	}

	@PutMapping("/counters")
	public Counters createTodo(@RequestBody Counters newCounters) {
		counters = newCounters;
		return counters;
	}
}
