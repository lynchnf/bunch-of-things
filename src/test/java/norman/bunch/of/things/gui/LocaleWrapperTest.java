package norman.bunch.of.things.gui;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class LocaleWrapperTest {
    @Test
    public void getLocale() {
        LocaleWrapper localeWrapperEnglish = new LocaleWrapper(Locale.ENGLISH);
        assertEquals(Locale.ENGLISH, localeWrapperEnglish.getLocale());
        assertEquals(Locale.ENGLISH.getDisplayName(), localeWrapperEnglish.toString());

        LocaleWrapper localeWrapperFrench = new LocaleWrapper(Locale.FRENCH);
        assertEquals(Locale.FRENCH, localeWrapperFrench.getLocale());
        assertEquals(Locale.FRENCH.getDisplayName(), localeWrapperFrench.toString());
    }

    @Test
    public void getLocaleNoArgConstructor() {
        LocaleWrapper localeWrapper = new LocaleWrapper();
        assertEquals(Locale.getDefault(), localeWrapper.getLocale());
        assertEquals(Locale.getDefault().getDisplayName(), localeWrapper.toString());
    }
}