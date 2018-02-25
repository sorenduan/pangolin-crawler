package org.pangolincrawler.core.cache;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pangolincrawler.core.PangolinApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PangolinApplication.class)
public class CacheManagerTest {

  @Autowired
  private CacheManager cacheManager;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {

    String key = "test_key";
    long v = cacheManager.increase(key, 50, TimeUnit.SECONDS);
    Assert.assertEquals(1, v);
    v = cacheManager.increase(key, 50, TimeUnit.SECONDS);
    Assert.assertEquals(2, v);

    v = cacheManager.increase(key, 1, TimeUnit.SECONDS);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    v = cacheManager.increase(key, 1, TimeUnit.SECONDS);
    Assert.assertFalse(1 == v);
  }
}
