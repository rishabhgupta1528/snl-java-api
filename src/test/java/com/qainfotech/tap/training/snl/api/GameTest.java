package com.qainfotech.tap.training.snl.api;

import org.testng.annotations.*;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author Ramandeep <RamandeepSingh@QAInfoTech.com>
 */
public class GameTest {
    
    Game snlGameAPI;
    
    @BeforeClass
    public void setUp(){
        snlGameAPI = new Game();
    }
    
    @Test
    public void firstTest(){
        assertThat(true).isEqualTo(true);
    }
}
