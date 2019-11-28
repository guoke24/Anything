package com.guohao.anything.net.gsonDemo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * A model object representing a cart that can be posted to an order-processing server
 *
 * @author Inderjeet Singh
 */
public class Cart {
    public final List<LineItem> lineItems;

    @SerializedName("buyer")
    private final String buyerName;

    private final String creditCard;

    public Cart(List<LineItem> lineItems, String buyerName, String creditCard) {
        this.lineItems = lineItems;
        this.buyerName = buyerName;
        this.creditCard = creditCard;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getCreditCard() {
        return creditCard;
    }

    @Override
    public String toString() {
        /**
         * 当内含列表类型的成员变量的时候，需要遍历每个元素，
         */

        StringBuilder itemsText = new StringBuilder();
        boolean first = true;
        if (lineItems != null) {
            try {
                // 得到一个域（成员变量）的类型的引用
                Class<?> fieldType = Cart.class.getField("lineItems").getType();
                // 输出该域（成员变量）的类型的名字
                System.out.println("LineItems CLASS: " + getSimpleTypeName(fieldType));
            } catch (SecurityException e) {
            } catch (NoSuchFieldException e) {
            }
            for (LineItem item : lineItems) {
                if (first) {
                    first = false;
                } else {
                    itemsText.append("; ");
                }
                itemsText.append(item);//此时会触发item的toString函数
            }
        }
        return "[BUYER: " + buyerName + "; CC: " + creditCard + "; "
                + "LINE_ITEMS: " + itemsText.toString() + "]";
    }

    @SuppressWarnings("unchecked")
    public static String getSimpleTypeName(Type type) {
        if (type == null) {
            return "null";
        }
        if (type instanceof Class) {
            return ((Class)type).getSimpleName();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            StringBuilder sb = new StringBuilder(getSimpleTypeName(pType.getRawType()));
            sb.append('<');
            boolean first = true;
            for (Type argumentType : pType.getActualTypeArguments()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append(getSimpleTypeName(argumentType));
            }
            sb.append('>');
            return sb.toString();
        } else if (type instanceof WildcardType) {
            return "?";
        }
        return type.toString();
    }

}
