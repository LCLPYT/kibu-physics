package work.lclpnet.kibu.physics.entity;

import com.mojang.math.Transformation;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import work.lclpnet.kibu.access.entity.DisplayEntityAccess;
import work.lclpnet.kibu.physics.api.EntityPhysicsElement;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.ElementRigidBody;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.EntityRigidBody;
import work.lclpnet.kibu.physics.impl.bullet.collision.body.shape.MinecraftShape;
import work.lclpnet.kibu.physics.impl.bullet.math.Convert;
import work.lclpnet.kibu.physics.util.BlockPhysics;

public class RigidBodyBlockEntity extends Display.BlockDisplay implements EntityPhysicsElement {

    private final EntityRigidBody rigidBody;
    private final com.jme3.math.Quaternion rotation = new com.jme3.math.Quaternion();
    private final Vector3f translation = new Vector3f();

    public RigidBodyBlockEntity(EntityType<? extends RigidBodyBlockEntity> type, Level level) {
        super(type, level);

        this.rigidBody = new EntityRigidBody(this);
        this.rigidBody.setBuoyancyType(ElementRigidBody.BuoyancyType.WATER);
        this.rigidBody.setMass(16);

        setTransformation(new Transformation(resetTranslation(), new Quaternionf(), new Vector3f(1), new Quaternionf()));
        setTransformationInterpolationDuration(type.updateInterval());
        setPosRotInterpolationDuration(type.updateInterval());
    }

    @Override
    public @Nullable EntityRigidBody getRigidBody() {
        return rigidBody;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) return;

        Quaternionf rotation = Convert.toMinecraft(this.getPhysicsRotation(this.rotation, 0));
        Vector3f translation = resetTranslation().rotate(rotation);

        var transform = new Transformation(translation, rotation, new Vector3f(1), new Quaternionf());
        DisplayEntityAccess.setTransformation(this, transform);

        setTransformationInterpolationDelay(0);
    }

    public void updateRigidBody() {
        BlockState state = getBlockState();

        this.getRigidBody().setMass(BlockPhysics.getMass(state));
        this.getRigidBody().setBuoyancyType(BlockPhysics.getBuoyancyType(state));
        this.rigidBody.setCollisionShape(this.createShape());
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    public MinecraftShape.Convex createShape() {
        return BlockPhysics.getShape(getBlockState(), level());
    }

    private Vector3f resetTranslation() {
        return translation.set(-0.5f);
    }
}
