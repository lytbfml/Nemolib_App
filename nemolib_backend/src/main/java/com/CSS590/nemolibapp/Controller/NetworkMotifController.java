package com.CSS590.nemolibapp.Controller;

import org.springframework.web.bind.annotation.*;

/**
 * @author Yangxiao on 3/5/2019.
 */
@RestController
public class NetworkMotifController {
	
	@GetMapping("/book")
	public String getBook() {
		return bookService.getBook();
	}
	
	@PostMapping("/book")
	public String addBook(@RequestParam String isbn, @RequestParam String title) {
		return bookService.addBook(isbn, title);
	}
	
	@RequestMapping(value = "req", method = RequestMethod.POST)
	public void addPromotion(@RequestParam(name = "gameid") int gameId,
	                         @RequestParam(name = "token") String token){
		paymentService.addPromotion(promotion);
	}
	
}
