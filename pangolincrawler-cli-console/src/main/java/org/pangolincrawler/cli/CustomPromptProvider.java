package org.pangolincrawler.cli;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class CustomPromptProvider implements PromptProvider {

	public AttributedString getPrompt() {
		return new AttributedString("pangolin> ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
	}
}