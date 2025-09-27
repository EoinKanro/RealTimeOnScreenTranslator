package io.github.eoinkanro.app.rtostranslator.process.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MessageWithData<T> implements Message {

  private final T data;

}
