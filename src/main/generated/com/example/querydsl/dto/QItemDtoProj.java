package com.example.querydsl.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.querydsl.dto.QItemDtoProj is a Querydsl Projection type for ItemDtoProj
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QItemDtoProj extends ConstructorExpression<ItemDtoProj> {

    private static final long serialVersionUID = 297593970L;

    public QItemDtoProj(com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<Integer> price, com.querydsl.core.types.Expression<Integer> stock) {
        super(ItemDtoProj.class, new Class<?>[]{String.class, int.class, int.class}, name, price, stock);
    }

}

