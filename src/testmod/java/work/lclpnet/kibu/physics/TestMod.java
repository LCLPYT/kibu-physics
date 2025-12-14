package work.lclpnet.kibu.physics;

import com.jme3.math.Vector3f;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import work.lclpnet.kibu.physics.entity.RigidBodyBlockEntity;
import work.lclpnet.kibu.physics.impl.bullet.math.Convert;

public class TestMod implements ModInitializer {

    public static final String MODID = "kibu-physics-test";

    public static final EntityType<RigidBodyBlockEntity> RIGID_BODY_BLOCK = registerEntityType(
            id("rigid_body_block"),
            EntityType.Builder.of(RigidBodyBlockEntity::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0, 0)
                    .clientTrackingRange(10)
                    .updateInterval(1)
    );

    public static @NotNull Identifier id(String floating) {
        return Identifier.fromNamespaceAndPath(MODID, floating);
    }

    private static <T extends Entity> EntityType<T> registerEntityType(Identifier id, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(ResourceKey.create(Registries.ENTITY_TYPE, id)));
    }

    @Override
    public void onInitialize() {
        UseItemCallback.EVENT.register((player, _level, hand) -> {
            if (!(_level instanceof ServerLevel level)) return InteractionResult.PASS;

            ItemStack stack = player.getItemInHand(hand);

            if (stack.is(Items.IRON_HORSE_ARMOR)) {
                var entity = new RigidBodyBlockEntity(RIGID_BODY_BLOCK, level);
                entity.setPos(player.getEyePosition().add(player.getLookAngle().scale(1)));
                entity.setBlockState(Blocks.EMERALD_BLOCK.defaultBlockState());
                entity.updateRigidBody();
                entity.getRigidBody().setLinearVelocity(Convert.toBullet(player.getLookAngle().scale(10)));
                entity.getRigidBody().setAngularVelocity(new Vector3f());
                entity.getRigidBody().setPhysicsLocation(Convert.toBullet(entity.position()));

                level.addFreshEntity(entity);
            }

            if (stack.is(Items.GOLDEN_HORSE_ARMOR)) {
                var entity = new Snowball(EntityType.SNOWBALL, level);
                entity.setPos(player.getEyePosition());
                entity.setDeltaMovement(player.getLookAngle());

                level.addFreshEntity(entity);
            }

            return InteractionResult.PASS;
        });
    }
}
