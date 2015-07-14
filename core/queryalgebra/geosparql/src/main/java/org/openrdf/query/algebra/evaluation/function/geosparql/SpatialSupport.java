package org.openrdf.query.algebra.evaluation.function.geosparql;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.context.SpatialContextFactory;
import com.spatial4j.core.shape.Point;
import com.spatial4j.core.shape.Shape;
import com.spatial4j.core.shape.ShapeCollection;

/**
 * This class is responsible for creating the
 * {@link com.spatial4j.core.context.SpatialContext},
 * {@link SpatialAlegbra}
 * and {@link WktWriter} that will be used.
 * It will first try to load a subclass of itself called "org.openrdf.query.algebra.evaluation.function.geosparql.SpatialSupportInitializer".
 * This is not provided, and is primarily intended as a way to inject JTS support.
 * If this fails then the following fall-backs are used:
 * <ul>
 * <li>a SpatialContext created by passing system properties with the prefix "spatialContext."
 * to {@link com.spatial4j.core.context.SpatialContextFactory}.
 * The prefix is stripped from the system property name to form the SpatialContextFactory argument name.</li>
 * <li>a SpatialAlgebra that does not support any operation.</li>
 * <li>a WktWriter that only supports points</li>.
 * </ul>
 */
abstract class SpatialSupport {
	private static final SpatialContext spatialContext;
	private static final SpatialAlgebra spatialAlgebra;
	private static final WktWriter wktWriter;

	static {
		SpatialSupport support;
		try {
			Class<?> cls = Class.forName("org.openrdf.query.algebra.evaluation.function.geosparql.SpatialSupportInitializer", true, Thread.currentThread().getContextClassLoader());
			support = (SpatialSupport) cls.newInstance();
		} catch (Exception e) {
			support = new DefaultSpatialSupport();
		}
		spatialContext = support.createSpatialContext();
		spatialAlgebra = support.createSpatialAlgebra();
		wktWriter = support.createWktWriter();
	}

	static SpatialContext getSpatialContext() {
		return spatialContext;
	}

	static SpatialAlgebra getSpatialAlgebra() {
		return spatialAlgebra;
	}

	static WktWriter getWktWriter() {
		return wktWriter;
	}

	protected abstract SpatialContext createSpatialContext();

	protected abstract SpatialAlgebra createSpatialAlgebra();

	protected abstract WktWriter createWktWriter();


	private static final class DefaultSpatialSupport extends SpatialSupport {
		private static final String SYSTEM_PROPERTY_PREFIX = "spatialContext.";

		@Override
		protected SpatialContext createSpatialContext() {
			Map<String,String> args = new HashMap<String,String>();
			for(String key : System.getProperties().stringPropertyNames()) {
				if(key.startsWith(SYSTEM_PROPERTY_PREFIX)) {
					args.put(key.substring(SYSTEM_PROPERTY_PREFIX.length()), System.getProperty(key));
				}
			}
			return SpatialContextFactory.makeSpatialContext(args, Thread.currentThread().getContextClassLoader());
		}

		@Override
		protected SpatialAlgebra createSpatialAlgebra() {
			return new DefaultSpatialAlgebra();
		}

		@Override
		protected WktWriter createWktWriter() {
			return new DefaultWktWriter();
		}
	}

	private static final class DefaultSpatialAlgebra implements SpatialAlgebra {

		private Shape notSupported() {
			throw new UnsupportedOperationException("Not supported due to licensing issues. Feel free to provide your own implementation by using something like JTS.");
		}

		@Override
		public Shape convexHull(Shape s) {
			return notSupported();
		}

		@Override
		public Shape boundary(Shape s) {
			if(s instanceof Point) {
				// points have no boundary so return empty shape
				return new ShapeCollection<Point>(Collections.<Point>emptyList(), getSpatialContext());
			}
			return notSupported();
		}

		@Override
		public Shape envelope(Shape s) {
			return notSupported();
		}

		@Override
		public Shape union(Shape s1, Shape s2) {
			if(s1 instanceof Point && s2 instanceof Point) {
				Point p1 = (Point) s1;
				Point p2 = (Point) s2;
				// order by x-ordinate
				if(p2.getX() < p1.getX()) {
					p1 = p2;
					p2 = (Point) s1;
				}
				return new ShapeCollection<Point>(Arrays.asList(p1, p2), getSpatialContext());
			}
			return notSupported();
		}

		@Override
		public Shape intersection(Shape s1, Shape s2) {
			return notSupported();
		}

		@Override
		public Shape symDifference(Shape s1, Shape s2) {
			return notSupported();
		}

		@Override
		public Shape difference(Shape s1, Shape s2) {
			return notSupported();
		}
		
	}

	private static final class DefaultWktWriter implements WktWriter {

		@Override
		public String toWkt(Shape shape) throws IOException {
			if(shape instanceof Point) {
				Point p = (Point) shape;
				return "POINT ("+p.getX()+" "+p.getY()+")";
			} else if(shape instanceof ShapeCollection<?>) {
				ShapeCollection<?> col = (ShapeCollection<?>) shape;
				if(col.isEmpty()) {
					return "MULTIPOINT EMPTY";
				}
				StringBuilder buf = new StringBuilder("MULTIPOINT (");
				String sep = "";
				for(Shape s : col) {
					if(!(s instanceof Point)) {
						throw new UnsupportedOperationException("This shape is not supported due to licensing issues. Feel free to provide your own implementation by using something like JTS.");
					}
					Point p = (Point) s;
					buf.append(sep);
					buf.append("(").append(p.getX()).append(" ").append(p.getY()).append(")");
					sep = ", ";
				}
				buf.append(")");
				return buf.toString();
			}
			throw new UnsupportedOperationException("This shape is not supported due to licensing issues. Feel free to provide your own implementation by using something like JTS.");
		}
		
	}
}
