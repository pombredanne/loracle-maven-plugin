package de.pfabulist.lisanity.model;

import org.apache.maven.artifact.Artifact;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Copyright (c) 2006 - 2016, Stephan Pfab
 * SPDX-License-Identifier: BSD-2-Clause
 */

public class KnownLicenses {

    private final Map<Coordinates, LiLicense> known = new HashMap<>();

    public KnownLicenses( Licenses licenses ) {
        known.put( new Coordinates( "net.jcip", "jcip-annotations", "1.0" ), licenses.getOrThrowByName( "CC-BY-2.5" ));
        known.put( new Coordinates( "org.apache.httpcomponents","httpclient","4.0.1" ), licenses.getOrThrowByName( "Apache-2.0" ));
    }

    public Optional<LiLicense> getLicense( Artifact arti ) {
        return Optional.ofNullable( known.get( Coordinates.fromArtifact( arti ) ) );
    }
}
