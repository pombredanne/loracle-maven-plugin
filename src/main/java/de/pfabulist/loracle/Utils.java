package de.pfabulist.loracle;

import de.pfabulist.loracle.buildup.JSONStartup;
import de.pfabulist.unchecked.Unchecked;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Copyright (c) 2006 - 2016, Stephan Pfab
 * SPDX-License-Identifier: BSD-2-Clause
 */

public class Utils {

    public static String getResourceAsString( String res ) {
        byte[] buf = new byte[ 3000000 ];

        int got = 0;
        try( @Nullable InputStream in = JSONStartup.class.getResourceAsStream( res) )  {
            if ( in == null ) {
                return "";
            }
            while( true ) {
                int once = in.read( buf, got, 3000000 - got );
                if( once < 0 ) {
                    break;
                }
                got += once;
            }
        } catch( IOException e ) {
            throw Unchecked.u( e );
        }

        return new String( buf, 0, got, StandardCharsets.UTF_8 );
    }


}