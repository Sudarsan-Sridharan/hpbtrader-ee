package com.highpowerbear.hpbtrader.linear.ibclient.classifier;

/**
 * Created by rkolar on 4/23/14.
 */

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface DefaultListener {
}