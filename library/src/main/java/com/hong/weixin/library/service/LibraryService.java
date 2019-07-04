package com.hong.weixin.library.service;

import org.springframework.data.domain.Page;

import com.hong.weixin.library.domain.Book;
import com.hong.weixin.library.domain.DebitList;

public interface LibraryService {

	Page<Book> search(String keyword, int pageNumber);

	void add(String id, DebitList list);

	void remove(String id, DebitList list);

}
