package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐，同时还要保持其和菜品等的所属关系
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }


    //删除套餐 并且一并删除与他绑定的菜品
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，判断是否可以被删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count=this.count(queryWrapper);
        if (count>0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐
        this.removeByIds(ids);
        //然后再删除单个菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

    }
    @Override
    public SetmealDto getByIdWithDishes(Long id) {
        //查询套餐基本信息，从setmeal表查询
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        //查询当前套餐对应的菜品信息，从setmeal_dish表查询
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(dishes);
        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDishes(SetmealDto setmealDto) {
        //更新setmeal表的基本信息
        this.updateById(setmealDto);
        //清理当前套餐对应的菜品数据---setmeal_dish表的delete操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //添加当前提交过来的菜品数据---setmeal_dish表的insert操作
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes = dishes.stream().map((item)->{
            item.setId(IdWorker.getId());
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(dishes);
    }

}
